package net.lastcraft.lobby.profile.gui.guis;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.inventory.type.MultiInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.scoreboard.Board;
import net.lastcraft.api.scoreboard.ScoreBoardAPI;
import net.lastcraft.api.usableitem.ClickAction;
import net.lastcraft.api.usableitem.ClickType;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.SoundType;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.gamer.constans.SettingsType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.dartaapi.guis.CustomItems;
import net.lastcraft.lobby.api.LobbyAPI;
import net.lastcraft.lobby.api.profile.BoardLobby;
import net.lastcraft.lobby.api.profile.ProfileGui;
import net.lastcraft.lobby.profile.hider.HiderItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SettingsPage extends ProfileGui {

    private static final ScoreBoardAPI SCORE_BOARD_API = LastCraft.getScoreBoardAPI();

    public SettingsPage(Player player) {
        super(player, "PROFILE_MAIN_ITEM_SETTINGS_NAME");
    }

    @Override
    protected void setItems() {
        setBackItem();

        setItem(inventory, 1, gamer, ItemUtil.getBuilder(Material.INK_SACK)
                .setDurability((short) 1)
                .build(), SettingsType.BLOOD, Group.GOLD);
        setItem(inventory, 2, gamer, new ItemStack(Material.FEATHER), SettingsType.FLY, Group.DIAMOND,
                (clicker, clickType, block) -> {
            if (!clicker.getWorld().getName().equals("lobby")) {
                return;
            }

            clicker.setAllowFlight(gamer.getSetting(SettingsType.FLY));
            clicker.setFlying(gamer.getSetting(SettingsType.FLY));
        });
        setItem(inventory, 3, gamer, new ItemStack(Material.WATCH), SettingsType.HIDER, (clicker, clickType, block) ->
                HiderItem.setSettings(clicker, gamer.getSetting(SettingsType.HIDER)));
        setItem(inventory, 4, gamer, new ItemStack(Material.PAPER), SettingsType.CHAT);
        setItem(inventory, 5, gamer, new ItemStack(Material.JUKEBOX), SettingsType.MUSIC, Group.EMERALD);
        setItem(inventory, 6, gamer, new ItemStack(Material.SIGN), SettingsType.BOARD, (clicker, clickType, block) -> {
            BoardLobby boardLobby = LobbyAPI.getBoardLobby();
            if (boardLobby == null) {
                return;
            }

            boolean enable = gamer.getSetting(SettingsType.BOARD);
            if (enable) {
                boardLobby.showBoard(gamer, gamer.getLanguage());
                return;
            }

            Board board = SCORE_BOARD_API.getBoard(clicker);
            if (board == null) {
                return;
            }

            board.remove();
        });
        setItem(inventory, 7, gamer, new ItemStack(Material.BOOK), SettingsType.PRIVATE_MESSAGE);
        setItem(inventory, 20, gamer, gamer.getHead(), SettingsType.FRIENDS_REQUEST);
        setItem(inventory, 21, gamer, ItemUtil.getBuilder(Material.SKULL_ITEM)
                .setDurability((short) 3)
                .build(), SettingsType.PARTY_REQUEST);
        setItem(inventory, 22, gamer, new ItemStack(Material.ENDER_PORTAL_FRAME), SettingsType.GUILD_REQUEST);
        setItem(inventory, 23, gamer, new ItemStack(Material.EXP_BOTTLE), SettingsType.DONATE_CHAT, Group.GOLD);

    }

    private static void setItem(MultiInventory inventory,
                                int slot,
                                BukkitGamer gamer,
                                ItemStack itemStack,
                                SettingsType settingsType,
                                Group minGroup) {
        setItem(inventory, slot, gamer, itemStack, settingsType, minGroup, null);
    }

    private static void setItem(MultiInventory inventory,
                                int slot,
                                BukkitGamer gamer,
                                ItemStack itemStack,
                                SettingsType settingsType) {
        setItem(inventory, slot, gamer, itemStack, settingsType, Group.DEFAULT);
    }

    private static void setItem(MultiInventory inventory,
                                int slot,
                                BukkitGamer gamer,
                                ItemStack itemStack,
                                SettingsType settingsType,
                                ClickAction clickAction) {
        setItem(inventory, slot, gamer, itemStack, settingsType, Group.DEFAULT, clickAction);
    }

    private static void setItem(MultiInventory inventory,
                                int slot,
                                BukkitGamer gamer,
                                ItemStack itemStack,
                                SettingsType settingsType,
                                Group minGroup,
                                ClickAction clickAction) {
        Language lang = gamer.getLanguage();
        boolean enable = gamer.getGroup().getLevel() >= minGroup.getLevel();

        List<String> lore = new ArrayList<>(lang.getList("LOBBY_SETTINGS_" + settingsType.name() + "_LORE"));
        if (!enable)
            lore.addAll(lang.getList("LOBBY_SETTINGS_UNAVAILABLE", minGroup.getNameEn()));

        inventory.setItem(slot, new DItem(ItemUtil.getBuilder(itemStack)
                .removeFlags()
                .setName((enable ? "§e" : "§c")
                        + lang.getMessage("LOBBY_SETTINGS_" + settingsType.name() + "_NAME"))
                .setLore(lore)
                .build()));

        inventory.setItem(slot + 9, new DItem(getEnableOrDisableItem(gamer, settingsType),
                (clicker, clickType, i) -> {
            if (Cooldown.hasOrAddCooldown(gamer, "click", 5)) {
                return;
            }

            if (!enable) {
                SOUND_API.play(clicker, SoundType.NO);
                return;
            }

            gamer.setSetting(settingsType, !gamer.getSetting(settingsType));
            SOUND_API.play(clicker, SoundType.POP);

            if (clickAction != null) {
                clickAction.onClick(clicker, (clickType.isRightClick() ? ClickType.RIGHT : ClickType.LEFT), null);
            }

            if (inventory.getInventories().isEmpty()) {
                return;
            }

            DItem dItem = inventory.getInventories().get(0).getItems().get(slot + 9);
            if (dItem == null) {
                return;
            }

            dItem.setItem(getEnableOrDisableItem(gamer, settingsType));
            inventory.setItem(slot + 9, dItem);
        }));
    }

    private static ItemStack getEnableOrDisableItem(BukkitGamer gamer, SettingsType settingsType) {
        Language lang = gamer.getLanguage();
        return (gamer.getSetting(settingsType) ? CustomItems.getEnable(lang) : CustomItems.getDisable(lang));
    }
}
