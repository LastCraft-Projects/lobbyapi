package net.lastcraft.lobby.profile.leveling;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.lastcraft.lobby.api.leveling.LevelReward;
import net.lastcraft.lobby.api.leveling.LevelRewardStorage;
import net.lastcraft.lobby.api.leveling.Leveling;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class LevelingImpl implements Leveling {

    private final TIntObjectMap<LevelRewardStorage> leveling = TCollections.synchronizedMap(new TIntObjectHashMap<>());

    @Override
    public List<LevelRewardStorage> getRewardsSorted() {
        return getRewards().valueCollection().stream()
                .sorted(Comparator.comparingInt(LevelRewardStorage::getLevel))
                .collect(Collectors.toList());
    }

    @Override
    public void addReward(int level, LevelReward... levelRewards) {
        LevelRewardStorage levelRewardStorage = leveling.get(level);
        if (levelRewardStorage == null) {
            levelRewardStorage = new LevelRewardStorage(level);
            leveling.put(level, levelRewardStorage);
        }

        levelRewardStorage.addLevelRewards(levelRewards);
    }

    @Override
    public TIntObjectMap<LevelRewardStorage> getRewards() {
        return new TIntObjectHashMap<>(leveling);
    }
}
