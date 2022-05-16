package net.lastcraft.lobby.api.leveling;

import gnu.trove.map.TIntObjectMap;

import java.util.List;

public interface Leveling {

    /**
     * получить все доступные награды
     * @return - награды
     */
    List<LevelRewardStorage> getRewardsSorted();

    void addReward(int level, LevelReward... levelRewards);

    TIntObjectMap<LevelRewardStorage> getRewards();
}
