package net.lastcraft.lobby.game.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.lastcraft.base.game.GameState;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(exclude = "name")
public final class Server {

    private final String name;
    private final String map;
    private final int online;
    private final GameState gameState;
    private final int maxPlayer;

    private final long timeUpdate;

    public boolean isAlive() {
        return System.currentTimeMillis() < timeUpdate + TimeUnit.SECONDS.toMillis(30);
    }
}
