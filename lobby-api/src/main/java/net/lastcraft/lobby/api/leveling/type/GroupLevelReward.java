package net.lastcraft.lobby.api.leveling.type;

import lombok.AllArgsConstructor;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.gamer.constans.PurchaseType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.box.type.GroupBox;
import net.lastcraft.lobby.api.leveling.LevelReward;

@AllArgsConstructor
public class GroupLevelReward extends LevelReward {

    private final Group group;

    @Override
    public void giveReward(BukkitGamer gamer) {
        if (gamer.getGroup().getId() >= group.getId()) {
            gamer.changeMoney(PurchaseType.GOLD, GroupBox.getMoney(group));
            return;
        }
        gamer.setGroup(group);
    }

    @Override
    public String getLore(Language language) {
        return "§8+ " + group.getNameEn() + " §8(§7" + language.getMessage("GROUP") + "§8)";
    }

    @Override
    public int getPriority() {
        return 1000 + group.getId();
    }
}
