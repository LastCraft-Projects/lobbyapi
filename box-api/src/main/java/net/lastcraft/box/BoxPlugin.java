package net.lastcraft.box;

import net.lastcraft.api.util.Rarity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.gamer.constans.KeyType;
import net.lastcraft.box.api.BoxAPI;
import net.lastcraft.box.api.ItemBoxManager;
import net.lastcraft.box.listener.BoxListener;
import net.lastcraft.box.type.GroupBox;
import net.lastcraft.box.type.KeysBox;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class BoxPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new BoxListener(this);

        ItemBoxManager itemBoxManager = BoxAPI.getItemBoxManager();
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new GroupBox(Group.GOLD, 4, Rarity.COMMON));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new GroupBox(Group.DIAMOND, 3, Rarity.RARE));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new GroupBox(Group.EMERALD, 5, Rarity.EPIC));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new GroupBox(Group.MAGMA, 14, Rarity.LEGENDARY));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new GroupBox(Group.SHULKER, 7, Rarity.LEGENDARY));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new KeysBox(2, Rarity.RARE, KeyType.GROUP_KEY));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new KeysBox(3, Rarity.EPIC, KeyType.GROUP_KEY));
        itemBoxManager.addItemBox(KeyType.GROUP_KEY, new KeysBox(5, Rarity.LEGENDARY, KeyType.GROUP_KEY));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
