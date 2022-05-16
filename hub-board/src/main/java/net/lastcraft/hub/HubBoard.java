package net.lastcraft.hub;

import net.lastcraft.hub.command.HorseCommand;
import net.lastcraft.hub.listeners.HorseListener;
import net.lastcraft.lobby.api.LobbyAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class HubBoard extends JavaPlugin {

    @Override
    public void onEnable() {
        LobbyAPI.setBoardLobby(new HBoard());

        new HorseListener(this);
        new HorseCommand(this);

        /*
        if (ChristmasThings.supports()) {
            this.christmasThings = new ChristmasThings(this);
            this.christmasThings.register();
        }
        */
    }


}