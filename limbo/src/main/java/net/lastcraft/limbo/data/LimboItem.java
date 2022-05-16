package net.lastcraft.limbo.data;

import net.lastcraft.base.locale.Language;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public final class LimboItem {

    private final ItemStack itemStack;

    public LimboItem(Language language, Material material) {
        this.itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        Arrays.stream(ItemFlag.values()).forEach(meta::addItemFlags);
        meta.setDisplayName("§a" + language.getMessage("LIMBO_ITEM_NAME"));
        meta.setLore(language.getList("LIMBO_ITEM_LORE"));
        meta.removeItemFlags();
        this.itemStack.setItemMeta(meta);
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }
}
