package net.lastcraft.rewards.bonuses;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.gamer.constans.KeyType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;

public class KeysBonus implements Bonus {

    @Override
    public void giveTo(BukkitGamer gamer, RewardType type, boolean chat) {
        int amount = getRandomCount(gamer.getGroup());
        Language lang = gamer.getLanguage();

        gamer.changeKeys(KeyType.DEFAULT_KEY, amount);

        if (chat) {
            gamer.sendMessage(StringUtil.stringToCenter(lang.getMessage(
                    "KEYS_REWARD_LOCALE",
                    StringUtil.getNumberFormat(amount),
                    StringUtil.getCorrectWord(amount, "KEYS_1",lang))));
        }
    }

    private int getRandomCount(Group group) {
        int baseBounds = 3;
        int minBounds = 1;

        baseBounds += group.getLevel();

        return RANDOM.nextInt(baseBounds) + minBounds;
    }

}
