package net.lastcraft.lobby.api.leveling.type;

import lombok.AllArgsConstructor;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.constans.KeyType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.lobby.api.leveling.LevelReward;

@AllArgsConstructor
public class KeysLevelReward extends LevelReward {

    private final KeyType keyType;
    private final int amount;

    @Override
    public void giveReward(BukkitGamer gamer) {
        gamer.changeKeys(keyType, amount);
    }

    @Override
    public String getLore(Language language) {
        return "§8+ §d" + StringUtil.getNumberFormat(amount)
                + " §7" + keyType.getName(language).substring(2);
    }

    @Override
    public int getPriority() {
        return 100 + keyType.getId();
    }
}
