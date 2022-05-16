package net.lastcraft.lobby.api;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.types.GameType;
import net.lastcraft.lobby.api.leveling.Leveling;
import net.lastcraft.lobby.api.profile.BoardLobby;
import net.lastcraft.lobby.api.profile.ProfileGui;
import net.lastcraft.lobby.profile.gui.ProfileGuiManagerImpl;
import net.lastcraft.lobby.profile.leveling.LevelingImpl;

@UtilityClass
public class LobbyAPI {

    private GuiManager<ProfileGui> profileGuiManager;
    private Leveling leveling;

    @Setter
    @Getter
    private BoardLobby boardLobby;

    public GuiManager<ProfileGui> getProfileGuiManager() {
        if (profileGuiManager == null) {
            profileGuiManager = new ProfileGuiManagerImpl();
        }
        return profileGuiManager;
    }

    public Leveling getLeveling() {
        if (leveling == null) {
            leveling = new LevelingImpl();
        }
        return leveling;
    }

    @Deprecated
    public boolean isOldGame() {
        GameType current = GameType.current;
        return current == GameType.BW || current == GameType.LW || current == GameType.KW || current == GameType.EW;
    }
}
