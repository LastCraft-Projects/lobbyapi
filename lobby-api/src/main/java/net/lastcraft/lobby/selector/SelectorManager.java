package net.lastcraft.lobby.selector;

import lombok.Getter;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.event.gamer.GamerChangeLanguageEvent;
import net.lastcraft.api.event.gamer.GamerChangePrefixEvent;
import net.lastcraft.api.event.gamer.async.AsyncGamerJoinEvent;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.locale.Language;
import net.lastcraft.connector.bukkit.Core;
import net.lastcraft.core.io.info.InfoFieldNames;
import net.lastcraft.core.io.info.ServerInfo;
import net.lastcraft.core.io.info.exception.FieldException;
import net.lastcraft.core.io.info.impl.ServerInfoImpl;
import net.lastcraft.core.io.info.mapper.ServerInfoMapping;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.lobby.Lobby;
import net.lastcraft.lobby.selector.command.GameMenuCommand;
import net.lastcraft.lobby.selector.command.SelectorCommand;
import net.lastcraft.lobby.selector.gamemenu.GameMenu;
import net.lastcraft.lobby.selector.selector.LobbySelector;
import net.lastcraft.serverstream.client.ServerFilterRequest;
import net.lastcraft.serverstream.client.ServerStreamer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class SelectorManager extends DListener<Lobby> {

    private final String nameServer;

    private final Map<String, GameMenu> gameMenus = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, LobbySelector> lobbySelectors = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, ServerInfo> lobbyServers = Collections.synchronizedMap(new TreeMap<>(
            Comparator.comparingInt(value -> Integer.valueOf(value.split("-")[1]))));

    public SelectorManager(Lobby lobby, ServerInfoMapping lobbyMapping) {
        super(lobby);

        nameServer = LastCraft.getCoreAPI().getServerName().split("-")[0];

        lobby.getServer().getScheduler().runTaskTimerAsynchronously(lobby, () -> {
            ServerInfo info = new ServerInfoImpl(ServerInfo.GLOBAL_VERSION, lobbyMapping.createFieldSet(ServerInfo.GLOBAL_VERSION));

            info.value(InfoFieldNames.SERVER_NAME, LastCraft.getCoreAPI().getServerName());
            info.value("playerIds", GAMER_MANAGER.getGamers()
                    .values()
                    .stream()
                    .map(IBaseGamer::getPlayerID)
                    .toArray(Integer[]::new));

            try {
                Core.sendServerInfo(info);
            } catch (FieldException e) {
                e.printStackTrace();
            }

            updateServers(lobby.getServerStreamer());
        }, 20L, 20L);

        new SelectorCommand(this);
        new GameMenuCommand(this);
    }

    private void updateServers(ServerStreamer serverStreamer) {
        gameMenus.values().forEach(GameMenu::updateItems); //обновляем гуи

        ServerFilterRequest request = ServerFilterRequest.newBuilder()
                //.limit(4) //макс кол-во серверов, что я смогу получить
                .addArgument(1, "(?i)" + nameServer + "-\\w+") //1 - ид фильтра на коре (под этим номером находится фильтр по регексу)
                .build((iterable, throwable) -> {

                    if (iterable != null) {
                        lobbyServers.clear();
                        for (ServerInfo info : iterable) {
                            lobbyServers.put(info.value(InfoFieldNames.SERVER_NAME), info);
                        }

                        lobbySelectors.values().forEach(LobbySelector::update);
                    }

                    if (throwable != null) {
                        throwable.printStackTrace();
                    }
                });
        serverStreamer.submit(request, Core.getConnectorChannel().getHandle());
    }

    public final GameMenu getGameMenu(BukkitGamer gamer) {
        String name = gamer.getName().toLowerCase();
        GameMenu gameMenu = gameMenus.get(name);
        if (gameMenu != null) {
            return gameMenu;
        }

        gameMenu = new GameMenu(gamer);
        gameMenus.put(name, gameMenu);
        return gameMenu;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(AsyncGamerJoinEvent e) {
        BukkitGamer gamer = e.getGamer();
        createSelector(gamer, gamer.getLanguage());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        String name = e.getPlayer().getName().toLowerCase();
        gameMenus.remove(name);
        lobbySelectors.remove(name);
    }

    @EventHandler
    public void onChangePrefix(GamerChangePrefixEvent e) {
        gameMenus.remove(e.getGamer().getName().toLowerCase());
    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        BukkitGamer gamer = e.getGamer();

        gameMenus.remove(gamer.getName().toLowerCase());
        createSelector(gamer, e.getLanguage());
    }

    private void createSelector(BukkitGamer gamer, Language language) {
        LobbySelector selector = new LobbySelector(gamer, language, this);
        lobbySelectors.put(gamer.getName().toLowerCase(), selector);
    }
}
