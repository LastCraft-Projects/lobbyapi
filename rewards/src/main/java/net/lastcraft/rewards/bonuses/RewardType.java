package net.lastcraft.rewards.bonuses;

import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Getter
public enum RewardType {

    DAILY(0, TimeUnit.DAYS.toMillis(1), "DAILY_REWARD_KEY", "DAILY_REWARD_LORE_KEY", Material.IRON_INGOT,
            new MoneyBonus()),
    WEEKLY(1, TimeUnit.DAYS.toMillis(7), "WEEKLY_REWARD_KEY", "WEEKLY_REWARD_LORE_KEY", Material.GOLD_INGOT,
            new MoneyBonus(), new ExpBonus()),
    MONTHLY(2, TimeUnit.DAYS.toMillis(30), "MONTHLY_REWARD_KEY", "MONTHLY_REWARD_LORE_KEY", Material.DIAMOND,
            new MoneyBonus(), new ExpBonus(), new KeysBonus()),
    //MONTHLY_DONATER(3, TimeUnit.DAYS.toMillis(30), "MONTHLY_REWARD_KEY", "MONTHLY_REWARD_LORE_KEY", Material.DIAMOND,
    //        new GoldBonus()),

    ;

    private final int id;
    private final long timeDelay;
    private final String localeKey;
    private final String localeLoreKey;
    private final Material item;
    private final Bonus[] rewards;

    RewardType(int id, long timeDelay, String localeKey, String localeLoreKey, Material item, Bonus... rewards) {
        this.id = id;
        this.timeDelay = timeDelay;
        this.localeKey = localeKey;
        this.localeLoreKey = localeLoreKey;
        this.item = item;
        this.rewards = rewards;
    }

    public int countRewards() {
        return this.rewards.length;
    }

    public static RewardType getRewardType(int id) {
        return Arrays.stream(values()).filter(rewardType -> rewardType.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
