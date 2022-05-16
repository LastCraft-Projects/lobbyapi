package net.lastcraft.lobby.api.leveling.type;

import lombok.AllArgsConstructor;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.constans.PurchaseType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.lobby.api.leveling.LevelReward;

@AllArgsConstructor
public class MoneyLevelReward extends LevelReward {

    private final int money;

    @Override
    public void giveReward(BukkitGamer gamer) {
        gamer.changeMoney(PurchaseType.MYSTERY_DUST, money);
    }

    @Override
    public String getLore(Language language) {
        return "§8+ §6" + StringUtil.getNumberFormat(money)
                + " §7" + StringUtil.getCorrectWord(money, "MONEY_2", language);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
