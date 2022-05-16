package net.lastcraft.lobby.profile.gui.guis;

import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.InventoryUtil;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.gamer.constans.PurchaseType;
import net.lastcraft.base.gamer.constans.SettingsType;
import net.lastcraft.base.gamer.sections.NetworkingSection;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.skin.Skin;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.dartaapi.donatemenu.guis.FastMessageGui;
import net.lastcraft.dartaapi.donatemenu.guis.JoinMessageGui;
import net.lastcraft.dartaapi.donatemenu.guis.PrefixGui;
import net.lastcraft.dartaapi.guis.basic.DonateGui;
import net.lastcraft.dartaapi.guis.basic.HelpGui;
import net.lastcraft.dartaapi.guis.basic.RewardHelpGui;
import net.lastcraft.dartaapi.loader.DartaAPI;
import net.lastcraft.lobby.api.profile.ProfileGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileMainPage extends ProfileGui {

    private static final DartaAPI DARTA_API = JavaPlugin.getPlugin(DartaAPI.class);

    public ProfileMainPage(Player player) {
        super(player);
    }

    @Override
    protected void setItems() {
        //todo выводить время в игре и выводить кол-во ачивок сделанных

        String skinName = gamer.getSkin() == Skin.DEFAULT_SKIN ? "N/A" : gamer.getSkin().getSkinName();
        inventory.setItem(InventoryUtil.getSlotByXY(2, 3), new DItem(ItemUtil.getBuilder(gamer.getHead())
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_PLAYER_NAME"))
                .setLore(lang.getList( "PROFILE_MAIN_ITEM_PLAYER_LORE",
                        (lang == Language.RUSSIAN ? gamer.getGroup().getName() : gamer.getGroup().getNameEn()),
                        StringUtil.getNumberFormat(gamer.getMoney(PurchaseType.MYSTERY_DUST)),
                        StringUtil.getNumberFormat(gamer.getMoney(PurchaseType.GOLD)),
                        StringUtil.getNumberFormat(gamer.getLevelNetwork()),
                        StringUtil.getNumberFormat(gamer.getExpNextLevel()),
                        String.valueOf(gamer.getFriends().size()),
                        String.valueOf(gamer.getFriendsLimit()),
                        skinName
                )).build()));

        inventory.setItem(InventoryUtil.getSlotByXY(3, 2), new DItem(ItemUtil.getBuilder(Material.BREWING_STAND_ITEM)
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_LEVEL_NAME"))
                .setLore(lang.getList("PROFILE_MAIN_ITEM_LEVEL_LORE",
                        StringUtil.getNumberFormat(gamer.getLevelNetwork()),
                        StringUtil.onPercentBar(NetworkingSection.getCurrentXPLVL(gamer.getExp()),
                                NetworkingSection.checkXPLVL(gamer.getLevelNetwork() + 1)),
                        StringUtil.onPercent(NetworkingSection.getCurrentXPLVL(gamer.getExp()),
                                NetworkingSection.checkXPLVL(gamer.getLevelNetwork() + 1)) + "%",
                        StringUtil.getNumberFormat(gamer.getExpNextLevel())
                )).build(), (pl, clickType, slot) -> {
            RewardLevelGui levelGui = GUI_MANAGER.getGui(RewardLevelGui.class, pl);
            if (levelGui == null) {
                return;
            }
            levelGui.open();
        }));

        inventory.setItem(InventoryUtil.getSlotByXY(4, 2), new DItem(ItemUtil.getBuilder(Material.PAPER)
                .setName("§b" + lang.getMessage( "PROFILE_MAIN_ITEM_STATS_NAME"))
                .setLore(lang.getList("PROFILE_MAIN_ITEM_STATS_LORE"))
                .build(), (pl, clickType, slot) -> pl.chat("/stats")));

        String enable =  "§a" + lang.getMessage("ENABLE");
        String disable =  "§c" + lang.getMessage( "DISABLE");
        inventory.setItem(InventoryUtil.getSlotByXY(4, 4), new DItem(ItemUtil.getBuilder(Material.REDSTONE_COMPARATOR)
                .setName("§b" + lang.getMessage( "PROFILE_MAIN_ITEM_SETTINGS_NAME"))
                .setLore(lang.getList("PROFILE_MAIN_ITEM_SETTINGS_LORE",
                        (gamer.getSetting(SettingsType.BLOOD) ? enable : disable),
                        (gamer.getSetting(SettingsType.FLY) ? enable : disable),
                        (gamer.getSetting(SettingsType.HIDER) ? enable : disable),
                        (gamer.getSetting(SettingsType.CHAT) ? enable : disable),
                        (gamer.getSetting(SettingsType.MUSIC) ? enable : disable),
                        (gamer.getSetting(SettingsType.BOARD) ? enable : disable),
                        (gamer.getSetting(SettingsType.PRIVATE_MESSAGE) ? enable : disable),
                        (gamer.getSetting(SettingsType.FRIENDS_REQUEST) ? enable : disable),
                        (gamer.getSetting(SettingsType.PARTY_REQUEST) ? enable : disable),
                        (gamer.getSetting(SettingsType.GUILD_REQUEST) ? enable : disable),
                        (gamer.getSetting(SettingsType.DONATE_CHAT) ? enable : disable)
                )).build(), (pl, clickType, slot) -> {
            SettingsPage settingsPage = GUI_MANAGER.getGui(SettingsPage.class, pl);
            if (settingsPage == null) {
                return;
            }
            settingsPage.open();
        }));

        inventory.setItem(9 * 2 + 8 - 1, new DItem(ItemUtil.getBuilder(Material.BOOK_AND_QUILL)
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_INFO_NAME"))
                .setLore(lang.getList( "PROFILE_MAIN_ITEM_INFO_LORE"))
                .build(), (pl, clickType, slot) ->
                DARTA_API.getGuiDefaultContainer().openGui(HelpGui.class, pl)));

        inventory.setItem(9 + 8 - 1, new DItem(ItemUtil.getBuilder(Material.ENCHANTMENT_TABLE)
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_DONATE_NAME"))
                .setLore(lang.getList("PROFILE_MAIN_ITEM_DONATE_LORE"))
                .build(), (pl, clickType, slot) ->
                DARTA_API.getGuiDefaultContainer().openGui(DonateGui.class, pl)));

        List<String> lore = new ArrayList<>(lang.getList("PROFILE_MAIN_ITEM_LANG_LORE1"));
        Arrays.stream(Language.values()).forEach(localization ->
                lore.add("§f ▪ " + (lang == localization ? "§a" : "")
                + localization.getName() + " §7(§c" + localization.getPercent() + "%§7)" ));
        lore.addAll(lang.getList( "PROFILE_MAIN_ITEM_LANG_LORE2"));
        inventory.setItem(InventoryUtil.getSlotByXY(5, 4), new DItem(ItemUtil.getBuilder(Head.WORLD)
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_LANG_NAME"))
                .setLore(lore)
                .build(), (pl, clickType, slot) -> {
            LangPage langPage = GUI_MANAGER.getGui(LangPage.class, pl);
            if (langPage == null) {
                return;
            }
            langPage.open();
        }));

        inventory.setItem(InventoryUtil.getSlotByXY(8, 4), new DItem(ItemUtil.getBuilder(Head.SHOP)
                .setName(lang.getMessage("GUI_HELP_ITEM_8_NAME"))
                .setLore(lang.getList("GUI_HELP_ITEM_8_LORE"))
                .build(), (pl, clickType, slot) ->
                DARTA_API.getGuiDefaultContainer().openGui(RewardHelpGui.class, pl)));

        inventory.setItem(InventoryUtil.getSlotByXY(3, 3), new DItem(ItemUtil.getBuilder(Head.BOOKS)
                .setName("§b" + lang.getMessage( "DONATE_MENU_FAST_MESSAGE_NAME"))
                .setLore(lang.getList("DONATE_MENU_FAST_MESSAGE_LORE"))
                .removeFlags()
                .build(), (pl, clickType, slot) -> DARTA_API.getDonateMenuListener().open(pl, FastMessageGui.class)));

        inventory.setItem(InventoryUtil.getSlotByXY(4, 3), new DItem(ItemUtil.getBuilder(Head.PREFIX)
                .setName("§b" + lang.getMessage( "DONATE_MENU_PREFIX_NAME"))
                .setLore(lang.getList("DONATE_MENU_PREFIX_LORE", gamer.getChatName()))
                .removeFlags()
                .build(), (pl, clickType, slot) -> DARTA_API.getDonateMenuListener().open(pl, PrefixGui.class)));

        inventory.setItem(InventoryUtil.getSlotByXY(5, 3), new DItem(ItemUtil.getBuilder(Head.JOIN_MESSAGE)
                .setName("§b" + lang.getMessage( "DONATE_MENU_JOIN_MESSAGE_NAME"))
                .setLore(lang.getList("DONATE_MENU_JOIN_MESSAGE_LORE"))
                .build(), (pl, clickType, slot) -> DARTA_API.getDonateMenuListener().open(pl, JoinMessageGui.class)));

        inventory.setItem(InventoryUtil.getSlotByXY(3, 4), new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                .setDurability((short) 14)
                .setName("§c???")
                .build()));

        inventory.setItem(InventoryUtil.getSlotByXY(5, 2), new DItem(ItemUtil.getBuilder(Material.NETHER_STAR)
                .setName("§b" + lang.getMessage("PROFILE_MAIN_ITEM_COSMETIC_NAME"))
                .setLore(lang.getList("PROFILE_MAIN_ITEM_COSMETIC_LORE"))
                .build(), (player1, clickType, i) -> player1.chat("/cosmetics")));
    }
}
