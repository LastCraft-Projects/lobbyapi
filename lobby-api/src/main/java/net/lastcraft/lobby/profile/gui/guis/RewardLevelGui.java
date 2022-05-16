package net.lastcraft.lobby.profile.gui.guis;

import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.util.InventoryUtil;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.SoundType;
import net.lastcraft.base.gamer.sections.NetworkingSection;
import net.lastcraft.lobby.api.LobbyAPI;
import net.lastcraft.lobby.api.leveling.LevelRewardStorage;
import net.lastcraft.lobby.api.leveling.Leveling;
import net.lastcraft.lobby.api.profile.ProfileGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class RewardLevelGui extends ProfileGui {

    private static final Leveling LEVELING = LobbyAPI.getLeveling();

    public RewardLevelGui(Player player) {
        super(player, "PROFILE_MAIN_ITEM_LEVEL_NAME");
    }

    @Override
    protected void setItems() {
        NetworkingSection section = gamer.getSection(NetworkingSection.class);
        List<LevelRewardStorage> rewards = LEVELING.getRewardsSorted();
        if (section == null || rewards.isEmpty()) {
            inventory.setItem(InventoryUtil.getSlotByXY(5, 3), new DItem(ItemUtil.getBuilder(Material.BARRIER)
                    .setName(lang.getMessage("LEVELING_LOAD_NAME"))
                    .setLore(lang.getList("LEVELING_LOAD_LORE"))
                    .build()));
            return;
        }

        int gamerLevel = gamer.getLevelNetwork();
        int giveRewardLevel = section.getGiveRewardLevel(); //за какой последний уровень выдана награда

        int slot = 10;
        int page = 0;
        for (LevelRewardStorage levelReward : rewards) { //они сразу будут отсортированы как надо
            int level = levelReward.getLevel();

            ChatColor chatColor = level > gamerLevel || level <= giveRewardLevel ? ChatColor.RED : ChatColor.GREEN;
            inventory.setItem(page, slot++, new DItem(ItemUtil.getBuilder(level <= giveRewardLevel ? Material.MINECART : Material.STORAGE_MINECART)
                    .setAmount(level)
                    .setName(chatColor.toString() + lang.getMessage("LEVEL_REWARD_NAME", level))
                    .setLore(lang.getList("LEVEL_REWARD_LORE1", level))
                    .addLore(levelReward.getLore(lang))
                    .addLore(level == giveRewardLevel + 1 ? lang.getList("LEVEL_REWARD_LORE2") : Collections.singletonList(" "))
                    .build(), (player, clickType, i) -> {
                        if (level > gamerLevel) {
                            gamer.sendMessageLocale("LEVEL_NO_LEVEL");
                            SOUND_API.play(player, SoundType.NO);
                            return;
                        }

                        if (level <= giveRewardLevel) {
                            gamer.sendMessageLocale("LEVEL_ALLREADY_GIVE");
                            SOUND_API.play(player, SoundType.NO);
                            return;
                        }

                        if (level != giveRewardLevel + 1) {
                            gamer.sendMessageLocale("LEVEL_NO_OTHER_REWARD");
                            SOUND_API.play(player, SoundType.NO);
                            return;
                        }

                        giveReward(section, levelReward);
                        SOUND_API.play(player, SoundType.SELECTED);
                        section.updateGiveRewardLevel();
                        update();
                    }));

            if ((slot - 8) % 9 == 0) {
                slot += 2;
            }

            if (slot >= 35) {
                slot = 10;
                page++;
            }
        }

        setBackItem();

        INVENTORY_API.pageButton(lang, page + 1, inventory, 38, 42);
    }

    private void giveReward(NetworkingSection section, LevelRewardStorage levelReward) {
        if (section.getGiveRewardLevel() >= levelReward.getLevel()) {
            return;
        }

        levelReward.giveRewards(gamer);
    }
}
