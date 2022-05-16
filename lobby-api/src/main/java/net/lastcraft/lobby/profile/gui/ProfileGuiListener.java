package net.lastcraft.lobby.profile.gui;

import net.lastcraft.api.event.gamer.GamerChangeLanguageEvent;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.lobby.Lobby;
import net.lastcraft.lobby.api.LobbyAPI;
import net.lastcraft.lobby.api.profile.ProfileGui;
import net.lastcraft.lobby.profile.commands.ProfileCommand;
import net.lastcraft.lobby.profile.commands.SettingsCommand;
import net.lastcraft.lobby.profile.gui.guis.LangPage;
import net.lastcraft.lobby.profile.gui.guis.ProfileMainPage;
import net.lastcraft.lobby.profile.gui.guis.RewardLevelGui;
import net.lastcraft.lobby.profile.gui.guis.SettingsPage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileGuiListener extends DListener<Lobby> {

    private final GuiManager<ProfileGui> manager = LobbyAPI.getProfileGuiManager();

    public ProfileGuiListener(Lobby lobby) {
        super(lobby);

        new ProfileCommand();
        new SettingsCommand();

        //создать все гуи
        manager.createGui(ProfileMainPage.class);
        manager.createGui(LangPage.class);
        manager.createGui(SettingsPage.class);
        manager.createGui(RewardLevelGui.class);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        manager.removeALL(e.getPlayer());
    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        Player player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }
        manager.removeALL(player);
    }
}
