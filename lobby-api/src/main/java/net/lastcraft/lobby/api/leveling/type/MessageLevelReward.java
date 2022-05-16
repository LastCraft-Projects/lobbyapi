package net.lastcraft.lobby.api.leveling.type;

import lombok.AllArgsConstructor;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.locale.Language;
import net.lastcraft.lobby.api.leveling.LevelReward;

@AllArgsConstructor
public class MessageLevelReward extends LevelReward {

    private final String key;

    @Override
    public void giveReward(BukkitGamer gamer) {
        //nothing
    }

    @Override
    public String getLore(Language language) {
        return language.getMessage(key);
    }
}
