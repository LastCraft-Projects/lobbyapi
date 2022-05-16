package net.lastcraft.lobby.api.leveling;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.locale.Language;

import java.util.*;

@RequiredArgsConstructor
public final class LevelRewardStorage {

    @Getter
    private final int level;
    private final Set<LevelReward> rewards = new ConcurrentSet<>();

    public List<String> getLore(Language lang) {
        List<String> lore = new ArrayList<>();
        rewards.stream()
                .sorted(Comparator.comparingInt(LevelReward::getPriority))
                .forEach(levelReward -> lore.add(" " + levelReward.getLore(lang)));
        return lore;
    }

    public void addLevelRewards(LevelReward... levelRewards) {
        rewards.addAll(Arrays.asList(levelRewards));
    }

    public void giveRewards(BukkitGamer gamer) {
        rewards.forEach(levelReward -> levelReward.giveReward(gamer));
    }
}
