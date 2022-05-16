package net.lastcraft.lobby.profile.gui;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.lobby.api.profile.ProfileGui;
import net.lastcraft.lobby.profile.gui.guis.ProfileMainPage;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileGuiManagerImpl implements GuiManager<ProfileGui> {

    @Getter
    private final Map<String, Map<String, ProfileGui>> playerGuis = new ConcurrentHashMap<>();
    private final Set<String> guis = new ConcurrentSet<>();

    @Override
    public void createGui(Class<? extends ProfileGui> clazz) {
        String name = clazz.getSimpleName().toLowerCase();
        if (guis.contains(name)) {
            return;
        }

        guis.add(name);
    }

    @Override
    public void removeGui(Class<? extends ProfileGui> clazz) {
        String nameClazz = clazz.getSimpleName().toLowerCase();
        for (String name : playerGuis.keySet()) {
            Map<String, ProfileGui> guis = playerGuis.get(name);
            for (String guiName : guis.keySet()) {
                if (guiName.equalsIgnoreCase(nameClazz)) {
                    guis.remove(guiName);
                }
            }
        }
    }

    @Override
    public <T extends ProfileGui> T getGui(Class<T> clazz, Player player) {
        String guiName = clazz.getSimpleName().toLowerCase();

        if (!guis.contains(guiName)) {
            return null;
        }

        T gui = null;

        String name = player.getName().toLowerCase();

        if (ProfileMainPage.class.getSimpleName().toLowerCase().equalsIgnoreCase(guiName)) {//основное гуи всегда
            ProfileMainPage profileMainPage = new ProfileMainPage(player);                  //обновлять надо и потому так
            profileMainPage.update();
            return (T) profileMainPage;
        }

        Map<String, ProfileGui> guis = playerGuis.get(name);
        if (guis == null) {
            guis = new ConcurrentHashMap<>();
            playerGuis.put(name, guis);
        }
        gui = (T) guis.get(guiName);

        if (gui == null) {
            try {
                gui = clazz.getConstructor(Player.class).newInstance(player);
                gui.update();
                guis.put(guiName, gui);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return gui;
    }

    @Override
    public void removeALL(Player player) {
        playerGuis.remove(player.getName().toLowerCase());
    }
}
