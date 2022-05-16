package net.lastcraft.lobby.game.old.top.data;

import lombok.Getter;
import net.lastcraft.api.util.Head;
import net.lastcraft.base.sql.GlobalLoader;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Deprecated
@Getter
public class TopPlayer {

    private final String displayName;
    private final ItemStack head;
    private final int stats;

    public TopPlayer(int stats, int playerID) {
        this.stats = stats;
        displayName = GlobalLoader.getDisplayName(playerID);
        String name = GlobalLoader.getName(playerID);
        String value = GlobalLoader.getSelectedSkin(name, playerID);
        if (value != null) {
            head = Head.getHeadByValue(value);
        } else {
            head = new ItemStack(Material.SKULL_ITEM);
            head.setDurability((short) 3);
        }
    }
}
