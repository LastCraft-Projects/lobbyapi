package net.lastcraft.lobby.game.data;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.types.SubType;
import net.lastcraft.base.game.GameState;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.connector.bukkit.Core;
import net.lastcraft.connector.bukkit.util.ScriptHelper;
import net.lastcraft.core.io.info.InfoFieldNames;
import net.lastcraft.core.io.info.ServerInfo;
import net.lastcraft.dartaapi.utils.core.CoreUtil;
import net.lastcraft.lobby.api.LobbyAPI;
import net.lastcraft.lobby.game.guis.ChannelGui;
import net.lastcraft.serverstream.client.ResponseCallback;
import net.lastcraft.serverstream.client.ServerFilterRequest;
import net.lastcraft.serverstream.client.ServerStreamer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public final class Channel {

    private final String nameChannel;
    private final SubType subType;

    private final TIntObjectMap<ChannelGui> guis = TCollections.synchronizedMap(new TIntObjectHashMap<>());

    public Channel(String nameChannel, SubType subType) {
        this.nameChannel = nameChannel;
        this.subType = subType;

        for (Language language : Language.values()) {
            guis.put(language.getId(), new ChannelGui(this, language));
        }
    }

    private final Map<String, Server> servers = Collections.synchronizedMap(new TreeMap<>(
            Comparator.comparingInt(value -> Integer.valueOf(value.split("-")[1]))));

    private final Map<String, ServersByMap> serversByMap = new ConcurrentHashMap<>();

    public ChannelGui getChannelGui(BukkitGamer gamer) {
        if (gamer == null) {
            return guis.get(Language.getDefault().getId());
        }

        int langId = gamer.getLanguage().getId();
        ChannelGui channelGui = guis.get(langId);
        if (channelGui != null) {
            return channelGui;
        }

        return guis.get(Language.getDefault().getId());
    }

    public int getOnline() {
        if (servers.isEmpty() || servers.values().stream().noneMatch(Server::isAlive)) {
            return -1;
        }

        return servers.values()
                .stream()
                .filter(Server::isAlive)
                .mapToInt(Server::getOnline)
                .sum();
    }

    public void sendToBestServer(BukkitGamer gamer) {

        if (LobbyAPI.isOldGame()) {
            Map<Server, Integer> servers = new HashMap<>();
            for (Server server : this.servers.values()) {
                if (server.isAlive() && server.getGameState() == GameState.WAITING) {
                    servers.put(server, server.getOnline());
                }
            }

            oldRedirect(gamer, servers);
            return;
        }

        Core.newScriptExecutor(
                "queue_" + gamer.getName(),
                "utils.queue(\"" + gamer.getName() + "\", \"" + nameChannel + "\");",
                ScriptHelper.emptyConsumer(),
                (f) -> null)
                .timeoutDuration(3 * 20)
                .deleteWhenExecuted()
                .subscribe()
                .execute();
    }

    @Deprecated //удалить потом
    private void oldRedirect(BukkitGamer gamer, Map<Server, Integer> servers) {
        servers = CoreUtil.sortByValue(servers); //отсортированы по онлайну


        if (Cooldown.hasOrAddCooldown(gamer, "connect", 20)) {
            return;
        }

        for (Server server : servers.keySet()) {
            if (server.getMaxPlayer() == server.getOnline()) {
                if (gamer.isDonater()) {
                    gamer.sendMessageLocale("REDIRECT_TO_SERVER", server.getName().toUpperCase());
                    net.lastcraft.connector.Core.redirect(gamer.getPlayer(), server.getName());
                    return;
                }
                continue;
            }
            gamer.sendMessageLocale("REDIRECT_TO_SERVER", server.getName().toUpperCase());
            net.lastcraft.connector.Core.redirect(gamer.getPlayer(), server.getName());
            return;
        }

        gamer.sendMessageLocale("NOT_BEST_SERVER");
    }

    public void sendToBestServer(BukkitGamer gamer, String map) {
        if (map == null) {
            return;
        }

        if (LobbyAPI.isOldGame()) {
            Map<Server, Integer> servers = new HashMap<>();
            for (Server server : this.servers.values()) {
                if (server.isAlive()
                        && server.getGameState() == GameState.WAITING
                        && server.getMap().equalsIgnoreCase(map)) {
                    servers.put(server, server.getOnline());
                }
            }

            oldRedirect(gamer, servers);

            return;
        }

        Core.newScriptExecutor(
                "queue_map_" + gamer.getName(),
                "utils.queue(\"" + gamer.getName() + "\", \"" + nameChannel + "\", \"" + map + "\");",
                ScriptHelper.emptyConsumer(),
                (f) -> null)
                .timeoutDuration(3 * 20)
                .deleteWhenExecuted()
                .subscribe()
                .execute();
    }

    public void update(ServerStreamer serverStreamer) {
        if (LobbyAPI.isOldGame()) {
            return;
        }

        ResponseCallback tracer = (iterable, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            for (ServerInfo info : iterable) {
                Server server = new Server(
                        info.value(InfoFieldNames.SERVER_NAME),
                        info.value("mapName"),
                        info.value(InfoFieldNames.PLAYERS_ONLINE),
                        info.value("gameState"),
                        info.value(InfoFieldNames.MAX_PLAYERS),
                        System.currentTimeMillis());

                servers.put(server.getName(), server);
            }
        };

        serversByMap.clear();
        for (Server server : servers.values()) {
            if (!server.isAlive()) {
                continue;
            }

            String map = server.getMap();
            ServersByMap servers = serversByMap.getOrDefault(map, new ServersByMap(this, map));
            if (servers.isEmpty()) {
                serversByMap.put(map, servers);
            }

            servers.put(server.getName(), server);
        }

        ServerFilterRequest request = ServerFilterRequest.newBuilder()
                //.limit(4) //макс кол-во серверов, что я смогу получить
                .addArgument(1, "(?i)" + nameChannel + "-\\w+") //1 - ид фильтра на коре (под этим номером находится фильтр по регексу)
                .build(tracer);
        serverStreamer.submit(request, Core.getConnectorChannel().getHandle());
    }

    public List<ServersByMap> getSortedServers() {
        return serversByMap.values().stream()
                .sorted(Comparator.comparing(ServersByMap::getMap))
                .sorted((sortedServers1, sortedServers2) -> {
                    if ((!sortedServers1.hasEmptyServers() && !sortedServers2.hasEmptyServers())
                            || (sortedServers1.hasEmptyServers() && sortedServers2.hasEmptyServers())) {
                        return 0;
                    }if (!sortedServers1.hasEmptyServers()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }).collect(Collectors.toList());
    }
}
