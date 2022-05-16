package net.lastcraft.lobby.profile.commands;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.lobby.api.LobbyAPI;
import net.lastcraft.lobby.api.profile.ProfileGui;
import net.lastcraft.lobby.profile.gui.guis.ProfileMainPage;
import org.bukkit.entity.Player;

public class ProfileCommand implements CommandInterface {

    private final GuiManager<ProfileGui> guiManager = LobbyAPI.getProfileGuiManager();

    public ProfileCommand() {
        SpigotCommand command = COMMANDS_API.register("profile", this, "профиль");
        command.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] strings) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        ProfileMainPage profileMainPage = guiManager.getGui(ProfileMainPage.class, player);
        if (profileMainPage == null) {
            return;
        }

        profileMainPage.open();
    }
}
