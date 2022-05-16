package net.lastcraft.lobby.game.data;

import lombok.Getter;
import net.lastcraft.base.game.GameState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class ServersByMap {

    private final Channel channel;
    private final String map;
    private final Map<String, Server> servers = new ConcurrentHashMap<>();

    private int empty = 0;

    public ServersByMap(Channel channel, String map) {
        this.channel = channel;
        this.map = map;
    }

    public boolean isEmpty() {
        return servers.isEmpty();
    }

    public void put(String name, Server newServer) {
        servers.put(name, newServer);

        int empty = servers.size();
        for (Server server : servers.values()) {
            if (server.getGameState() != GameState.WAITING) {
                empty--;
            }
        }

        this.empty = empty;
    }

    public boolean hasEmptyServers() {
        return empty > 0;
    }

    public int size() { //кол-во серверов с этой картой
        return servers.size();
    }
}
