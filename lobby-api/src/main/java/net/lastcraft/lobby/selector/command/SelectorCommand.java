package net.lastcraft.lobby.selector.command;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.lobby.selector.SelectorManager;
import net.lastcraft.lobby.selector.selector.LobbySelector;
import org.bukkit.entity.Player;

public class SelectorCommand implements CommandInterface {

    private final SelectorManager manager;

    public SelectorCommand(SelectorManager manager) {
        this.manager = manager;
        SpigotCommand spigotCommand = COMMANDS_API.register("selector", this);
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        LobbySelector lobbySelector = manager.getLobbySelectors().get(player.getName().toLowerCase());
        if (lobbySelector == null) {
            return;
        }

        lobbySelector.update();
        lobbySelector.getInventory().openInventory(player);
    }
}
