package net.lastcraft.box.type;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.util.Rarity;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.box.api.ItemBox;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class XpBox extends ItemBox {

    private final int exp;

    public XpBox(int exp, Rarity rarity) {
        super(new ItemStack(Material.EXP_BOTTLE), rarity);
        this.exp = exp;
    }

    @Override
    public String getName(Language lang) {
        return lang.getMessage("BOX_EXP", StringUtil.getNumberFormat(exp));
    }

    @Override
    public void onApply(BukkitGamer gamer) {
        gamer.addExp(exp);
    }

    @Override
    public void onMessage(BukkitGamer gamer) {
        gamer.sendActionBar("§a+" + exp + " XP");
    }
}
