package net.lastcraft.rewards.bonuses;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.lobby.utils.LevelUtils;

public class ExpBonus implements Bonus {

    @Override
    public void giveTo(BukkitGamer gamer, RewardType type, boolean chat) {
        int amount = getRandomCount(type, gamer.getGroup());
        Language lang = gamer.getLanguage();

        gamer.addExp(amount);

        LevelUtils.setExpData(gamer);

        if (chat) {
            gamer.sendMessage(StringUtil.stringToCenter(lang.getMessage(
                    "EXP_REWARD_LOCALE", StringUtil.getNumberFormat(amount))));
        }
    }

    private int getRandomCount(RewardType type, Group group) {
        int baseBounds = 50;
        int minBounds = 100;

        if (type == RewardType.WEEKLY) {
            baseBounds = 200;
            minBounds = 200;
        } else if (type == RewardType.MONTHLY) {
            baseBounds = 300;
            minBounds = 350;
        }

        baseBounds += group.getLevel() * 50;

        return RANDOM.nextInt(baseBounds) + minBounds;
    }

}
