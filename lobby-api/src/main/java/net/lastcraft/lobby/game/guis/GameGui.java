package net.lastcraft.lobby.game.guis;

import net.lastcraft.api.CoreAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.type.MultiInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.base.locale.Language;

public abstract class GameGui {

    protected static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    protected static final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    protected static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();
    protected static final CoreAPI CORE_API = LastCraft.getCoreAPI();

    protected final MultiInventory multiInventory;
    protected final Language lang;

    protected GameGui(String key, Language lang, Object... replaced) {
        this.multiInventory = INVENTORY_API.createMultiInventory(lang.getMessage(key, replaced), 5);
        this.lang = lang;
    }

    public final void update() {
        if (multiInventory == null) {
            return;
        }

        setItems();
    }

    protected abstract void setItems();

    public final void open(BukkitGamer gamer) {
        if (multiInventory == null) {
            return;
        }

        multiInventory.openInventory(gamer);
    }
}
