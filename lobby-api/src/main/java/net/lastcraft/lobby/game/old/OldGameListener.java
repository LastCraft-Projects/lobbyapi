package net.lastcraft.lobby.game.old;

import net.lastcraft.base.game.GameState;
import net.lastcraft.connector.Core;
import net.lastcraft.connector.events.CoreMessageEvent;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.lobby.Lobby;
import net.lastcraft.lobby.config.GameConfig;
import net.lastcraft.lobby.game.data.Channel;
import net.lastcraft.lobby.game.data.Server;
import net.lastcraft.lobby.game.data.ServersByMap;
import org.bukkit.event.EventHandler;

@Deprecated //удалить за ненадобностью потом
public class OldGameListener extends DListener<Lobby> {

    private final GameConfig gameConfig;

    public OldGameListener(Lobby javaPlugin, GameConfig gameConfig) {
        super(javaPlugin);

        this.gameConfig = gameConfig;

        new Core(); //инициализируем подключение к старому кору...
    }

    @EventHandler
    public void onCoreMessage(CoreMessageEvent e) {
        String serverName = e.getSender();

        if (!serverName.contains("-")) {
            return;
        }

        String channelName = e.getTag();
        Channel channel = gameConfig.getChannels().get(channelName.toLowerCase());
        if (channel == null) {
            return;
        }

        String message = e.getMessage();
        String[] messageSplit = message.split(";");

        String map = messageSplit[0];
        int online = Integer.parseInt(messageSplit[1]);
        int slots = Integer.parseInt(messageSplit[2]);
        GameState status = !Boolean.valueOf(messageSplit[3]) ? GameState.WAITING : GameState.GAME;

        Server server = new Server(serverName, map, online,
                status, slots, System.currentTimeMillis());

        channel.getServers().put(serverName, server);

        ServersByMap servers = channel.getServersByMap().getOrDefault(map, new ServersByMap(channel, map));
        if (servers.isEmpty()) {
            channel.getServersByMap().put(map, servers);
        }

        servers.put(serverName, server);
    }
}
