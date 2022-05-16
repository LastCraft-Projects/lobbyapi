package net.lastcraft.box.type;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.util.Rarity;
import net.lastcraft.base.gamer.constans.PurchaseType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.box.api.ItemBox;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MoneyBox extends ItemBox {

    private final int money;

    public MoneyBox(int money, Rarity rarity) {
        super(new ItemStack(Material.DOUBLE_PLANT), rarity);
        this.money = money;
    }

    @Override
    public String getName(Language lang) {
        return lang.getMessage("BOX_MONEY", StringUtil.getNumberFormat(money));
    }

    @Override
    public void onApply(BukkitGamer gamer) {
        gamer.changeMoney(PurchaseType.MYSTERY_DUST, money);
    }

    @Override
    public void onMessage(BukkitGamer gamer) {
        gamer.sendActionBar("§6+" + money + " "
                + StringUtil.getCorrectWord(money, "MONEY_1", gamer.getLanguage()));
    }
}
