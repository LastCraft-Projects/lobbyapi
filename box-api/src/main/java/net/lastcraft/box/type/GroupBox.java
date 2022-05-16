package net.lastcraft.box.type;

import io.netty.util.internal.ConcurrentSet;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.api.util.Rarity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.gamer.constans.PurchaseType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.box.api.ItemBox;
import org.bukkit.Material;

import java.util.Set;

public class GroupBox extends ItemBox {

    public static final Set<String> CHANGED_GROUPS = new ConcurrentSet<>();

    private final Group group;

    public GroupBox(Group group, int color, Rarity rarity) {
        super(ItemUtil.getBuilder(Material.STAINED_GLASS)
                .setDurability((short) color)
                .build(), rarity);
        this.group = group;
    }

    @Override
    public String getName(Language lang) {
        return group.getNameEn() + " §f(" + lang.getMessage("GROUP") + ")" ;
    }

    @Override
    public void onApply(BukkitGamer gamer) {
        if (gamer.getGroup().getId() >= group.getId()) {
            gamer.changeMoney(PurchaseType.GOLD, getMoney(group));
            return;
        }

        CHANGED_GROUPS.add(gamer.getName().toLowerCase());
        gamer.setGroup(group);
    }

    @Override
    public void onMessage(BukkitGamer gamer) {
        if (CHANGED_GROUPS.remove(gamer.getName().toLowerCase())) {
            return;
        }

        Language lang = gamer.getLanguage();
        int money = getMoney(group);
        String moneyString = StringUtil.getCorrectWord(money, "GOLD_1", lang);
        gamer.sendMessage("§b" + lang.getMessage("BOX_NAME")
                + " §8| " + lang.getMessage("GROUP_ALREADY_HAVE",
                StringUtil.getNumberFormat(money), moneyString));
    }

    public static int getMoney(Group group) {
        int money = 10;
        switch (group) {
            default:
                return money;
            case DIAMOND:
            case EMERALD:
                return money + 10;
            case SHULKER:
            case MAGMA:
                return money + 30;
        }
    }
}
