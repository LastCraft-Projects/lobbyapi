package net.lastcraft.rewards.bonuses;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.gamer.constans.PurchaseType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;

public class MoneyBonus implements Bonus {

    @Override
    public void giveTo(BukkitGamer gamer, RewardType type, boolean chat) {
        int amount = getRandomCount(type, gamer.getGroup());
        Language lang = gamer.getLanguage();

        gamer.changeMoney(PurchaseType.MYSTERY_DUST, amount);

        if (chat) {
            gamer.sendMessage(StringUtil.stringToCenter(lang.getMessage("MONEY_REWARD_LOCALE",
                    StringUtil.getNumberFormat(amount),
                    StringUtil.getCorrectWord(amount, "MONEY_1",lang))));
        }
    }

    private int getRandomCount(RewardType type, Group group) {
        int baseBounds = 100;
        int minBounds = 200;

        if (type == RewardType.WEEKLY) {
            baseBounds = 300;
            minBounds = 300;
        } else if (type == RewardType.MONTHLY) {
            baseBounds = 700;
            minBounds = 450;
        }

        baseBounds += group.getLevel() * 100;

        return RANDOM.nextInt(baseBounds) + minBounds;
    }
}
