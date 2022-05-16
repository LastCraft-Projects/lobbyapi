package net.lastcraft.lobby.api.profile;

import lombok.Getter;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.type.MultiInventory;
import net.lastcraft.api.manager.GuiManager;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.base.SoundType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.dartaapi.guis.CustomItems;
import net.lastcraft.lobby.api.LobbyAPI;
import net.lastcraft.lobby.profile.gui.guis.ProfileMainPage;
import org.bukkit.entity.Player;

@Getter
public abstract class ProfileGui {

    protected static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();
    protected static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    protected static final GuiManager<ProfileGui> GUI_MANAGER = LobbyAPI.getProfileGuiManager();
    protected static final SoundAPI SOUND_API = LastCraft.getSoundAPI();

    protected final BukkitGamer gamer;

    protected Language lang;
    protected MultiInventory inventory;

    protected ProfileGui(Player player) {
        gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        lang = gamer.getLanguage();

        inventory = INVENTORY_API.createMultiInventory(player, lang.getMessage("PROFILE_MAIN_GUI_NAME"), 5);
    }

    protected ProfileGui(Player player, String key) {
        gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        lang = gamer.getLanguage();

        inventory = INVENTORY_API.createMultiInventory(player,
                lang.getMessage("PROFILE_MAIN_GUI_NAME") + " ▸ " + lang.getMessage(key), 5);
    }

    protected final void setBackItem() {
        inventory.setItem(40, new DItem(CustomItems.getBack(lang), //кнопка назад
                (clicker, clickType, slot) -> {
                    SOUND_API.play(clicker, SoundType.PICKUP);
                    ProfileMainPage mainPage = GUI_MANAGER.getGui(ProfileMainPage.class, clicker);
                    if (mainPage == null) {
                        return;
                    }

                    mainPage.open();
                }));
    }

    public final void update() {
        if (gamer == null || inventory == null) {
            return;
        }

        setItems();
    }

    protected abstract void setItems();

    public void open() {
        if (gamer == null || inventory == null) {
            return;
        }

        inventory.openInventory(gamer);
    }


}
