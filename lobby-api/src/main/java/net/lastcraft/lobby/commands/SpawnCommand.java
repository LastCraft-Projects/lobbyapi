package net.lastcraft.lobby.commands;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.lobby.config.SettingConfig;

public class SpawnCommand implements CommandInterface {

    private final SettingConfig settingConfig;

    public SpawnCommand(SettingConfig settingConfig) {
        this.settingConfig = settingConfig;

        SpigotCommand spigotCommand = COMMANDS_API.register("spawn", this, "home");
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        ((BukkitGamer) gamerEntity).getPlayer().teleport(settingConfig.getSpawn());
    }
}
