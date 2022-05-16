package net.lastcraft.lobby.selector.command;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.lobby.selector.SelectorManager;
import net.lastcraft.lobby.selector.gamemenu.GameMenu;

public class GameMenuCommand implements CommandInterface {

    private final SelectorManager manager;

    public GameMenuCommand(SelectorManager manager) {
        this.manager = manager;
        SpigotCommand spigotCommand = COMMANDS_API.register("gamemenu", this);
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;

        GameMenu gameMenu = manager.getGameMenu(gamer);
        if (gameMenu == null) {
            return;
        }

        gameMenu.open();
    }
}
