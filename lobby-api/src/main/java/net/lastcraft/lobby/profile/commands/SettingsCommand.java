package net.lastcraft.lobby.profile.commands;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.lobby.api.LobbyAPI;
import net.lastcraft.lobby.api.profile.ProfileGui;
import net.lastcraft.lobby.profile.gui.guis.SettingsPage;
import org.bukkit.entity.Player;

public class SettingsCommand implements CommandInterface {

    private final GuiManager<ProfileGui> guiManager = LobbyAPI.getProfileGuiManager();

    public SettingsCommand() {
        SpigotCommand command = COMMANDS_API.register("settings", this, "настройки");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        SettingsPage page = guiManager.getGui(SettingsPage.class, player);
        if (page == null) {
            return;
        }

        page.open();
    }
}
