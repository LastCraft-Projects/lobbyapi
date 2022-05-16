package net.lastcraft.lobby.commands;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.lobby.Lobby;

public class ReloadConfigCommand implements CommandInterface {

    private final Lobby lobby;

    public ReloadConfigCommand(Lobby lobby) {
        this.lobby = lobby;

        SpigotCommand spigotCommand = COMMANDS_API.register("lobbyreload", this);
        spigotCommand.setMinimalGroup(Group.ADMIN);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        lobby.reloadConfig();
        gamerEntity.sendMessage("§6Lobby §8| §fКонфиг перезагружен!");
    }
}
