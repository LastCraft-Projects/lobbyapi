package net.lastcraft.rewards.data;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.type.DInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.TimeUtil;
import net.lastcraft.rewards.Rewards;
import net.lastcraft.rewards.bonuses.RewardType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class RewardGui {

    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();

    private final Player player;
    private final Rewards rewards;

    private DInventory inventory;

    public RewardGui(Player player, Rewards rewards) {
        this.player = player;
        this.rewards = rewards;

        createInventory();
        update();
    }

    public void createInventory() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player.getName());
        if (gamer == null)
            return;
        this.inventory = INVENTORY_API.createInventory(gamer.getLanguage().getMessage("REWARD_GUI_NAME"), 5);
    }

    public void update() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player.getName());
        if (gamer == null)
            return;

        Language language = gamer.getLanguage();
        RewardPlayer rewardPlayer = RewardPlayer.getPlayer(player.getName());

        if (rewardPlayer == null)
            return;

        int slot = 10;

        inventory.clearInventory();

        for (RewardType type : RewardType.values()) {
            long rewardTime = rewardPlayer.getActivationTime(type) + type.getTimeDelay();
            boolean available = rewardTime < System.currentTimeMillis();

            String claimTime = TimeUtil.leftTime(language, rewardTime, true);

            List<String> lore = new ArrayList<>(language.getList(type.getLocaleLoreKey()));
            List<String> actionLore = language.getList("REWARD_" +
                    (available ? "CLICK_TO_CLAIM" : "YOU_CAN_CLAIM_IN"), claimTime);
            lore.addAll(actionLore);

            ItemStack rewardItem = ItemUtil.getBuilder(available ? type.getItem() : Material.STAINED_GLASS_PANE)
                                            .setName("§e" + language.getMessage(type.getLocaleKey()))
                                            .setLore(lore)
                                            .setDurability((short) (available ? 0 : 14))
                                            .addFlag(ItemFlag.HIDE_ENCHANTS)
                                            .build();

            inventory.setItem(slot, new DItem(rewardItem, (target, clickType, clickedSlot) -> {
                if (available) {
                    player.closeInventory();
                    rewardPlayer.activateReward(type, target, rewards);
                }
            }));

            slot += 2;
        }

        ItemStack voteItem = ItemUtil.getBuilder(Material.BOOK)
                                .setName(language.getMessage("REWARD_VOTE_ITEM"))
                                .setLore(language.getList("REWARD_VOTE_ITEM_LORE"))
                                .build();

        ItemStack donateItem = ItemUtil.getBuilder(Material.EXP_BOTTLE)
                                .setName(language.getMessage("REWARD_DONATE_ITEM"))
                                .setLore(language.getList("REWARD_DONATE_ITEM_LORE"))
                                .build();

        inventory.setItem(30, new DItem(voteItem));
        inventory.setItem(32, new DItem(donateItem));
    }

    public void open() {
        if (inventory == null)
            return;
        inventory.openInventory(player);
    }

}
