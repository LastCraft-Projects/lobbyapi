package net.lastcraft.lobby.game.old.stats;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.type.DInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.util.Head;
import net.lastcraft.base.SoundType;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.dartaapi.guis.CustomItems;
import net.lastcraft.dartaapi.utils.inventory.ItemUtil;
import net.lastcraft.lobby.api.LobbyAPI;
import net.lastcraft.lobby.profile.gui.guis.ProfileMainPage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Deprecated
public class OldStatsGui {

    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();

    private final IBaseGamer statsOwner;
    private final DInventory inventory;

    public OldStatsGui(IBaseGamer statsOwner, BukkitGamer guest) {
        this.statsOwner = statsOwner;
        Language language = guest.getLanguage();
        if (statsOwner.getPlayerID() == guest.getPlayerID()) {
            inventory = INVENTORY_API.createInventory(guest.getPlayer(), language.getMessage("PROFILE_MAIN_GUI_NAME") + " ▸ " + language.getMessage("PROFILE_STATS_GUI_NAME"),5);
        } else {
            inventory = INVENTORY_API.createInventory(guest.getPlayer(), language.getMessage("PROFILE_STATS_GUI_NAME2") + " - " + statsOwner.getName(), 5);
        }
    }

    public void open(Player player) {
        inventory.openInventory(player);
    }

    public void load() {
        Map<String, int[]> stats = StatsUtil.loadStats(statsOwner.getPlayerID());

        int[] bedwarsteam = stats.get("BWT");
        int[] bedwarsdoubles = stats.get("BWD");
        int[] bedwarssolo = stats.get("BWS");
        int[] eggwarssolo = stats.get("EWS");
        int[] eggwarsdoubles = stats.get("EWD");
        int[] eggwarsteam = stats.get("EWT");
        int[] kitwarssolo = stats.get("KWS");
        int[] kitwarsteam = stats.get("KWT");
        int[] luckywarssolo = stats.get("LWS");
        int[] luckywarsteam = stats.get("LWT");

        if (kitwarssolo != null && kitwarsteam != null){
            inventory.setItem(11, new DItem(ItemUtil.setItemMeta(Head.BATMAN.getHead(), "§bKitWars", Arrays.asList(
                    "§fОдиночный режим:",
                    " §8▪ §7Сыграно игр: §e" + StringUtil.getNumberFormat(kitwarssolo[0]),
                    " §8▪ §7Побед: §e" + StringUtil.getNumberFormat(kitwarssolo[1]),
                    " §8▪ §7Убийств: §e" + StringUtil.getNumberFormat(kitwarssolo[2]),
                    "§fКомандный режим:",
                    " §8▪ §7Сыграно игр: §e" + StringUtil.getNumberFormat(kitwarsteam[0]),
                    " §8▪ §7Побед: §e" + StringUtil.getNumberFormat(kitwarsteam[1]),
                    " §8▪ §7Убийств: §e" + StringUtil.getNumberFormat(kitwarsteam[2]
                    )))));
        } else {
            inventory.setItem(11, new DItem(ItemUtil.setItemMeta(Head.BATMAN.getHead(), "§bKitWars", Collections.singletonList("§cОшибка загрузки данных..."))));
        }

        if (luckywarssolo != null && luckywarsteam != null){
            inventory.setItem(12, new DItem(ItemUtil.setItemMeta(Head.LUCKY.getHead(), "§bLuckyWars", Arrays.asList(
                    "§fОдиночный режим:",
                    " §8▪ §7Сыграно игр: §e" + StringUtil.getNumberFormat(luckywarssolo[0]),
                    " §8▪ §7Побед: §e" + StringUtil.getNumberFormat(luckywarssolo[1]),
                    " §8▪ §7Убийств: §e" + StringUtil.getNumberFormat(luckywarssolo[2]),
                    " §8▪ §7Сломано ЛакиБлоков: §e" + StringUtil.getNumberFormat(luckywarssolo[3]),
                    "§fКомандный режим:",
                    " §8▪ §7Сыграно игр: §e" + StringUtil.getNumberFormat(luckywarsteam[0]),
                    " §8▪ §7Побед: §e" + StringUtil.getNumberFormat(luckywarsteam[1]),
                    " §8▪ §7Убийств: §e" + StringUtil.getNumberFormat(luckywarsteam[2]),
                    " §8▪ §7Сломано ЛакиБлоков: §e" + StringUtil.getNumberFormat(luckywarsteam[3]
                    )))));
        } else {
            inventory.setItem(12, new DItem(ItemUtil.setItemMeta(Head.LUCKY.getHead(), "§bLuckyWars", Collections.singletonList("§cОшибка загрузки данных..."))));
        }

        if (bedwarsteam != null && bedwarsdoubles != null && bedwarssolo != null){
            inventory.setItem(13, new DItem(ItemUtil.setItemMeta(new ItemStack(Material.BED), "§bBedWars", Arrays.asList(
                    "§fОдиночный режим:",
                    " §8▪ §7Сыграно игр: §e" + StringUtil.getNumberFormat(bedwarssolo[0]),
                    " §8▪ §7Побед: §e" + StringUtil.getNumberFormat(bedwarssolo[1]),
                    " §8▪ §7Финальных убийств: §e" + StringUtil.getNumberFormat(bedwarssolo[2]),
                    " §8▪ §7Убийств: §e" + StringUtil.getNumberFormat(bedwarssolo[4]),
                    " §8▪ §7Сломано кроватей: §e" + StringUtil.getNumberFormat(bedwarssolo[3]),
                    "§fПарный режим:",
                    " §8▪ §7Сыграно игр: §e" + StringUtil.getNumberFormat(bedwarsdoubles[0]),
                    " §8▪ §7Побед: §e" + StringUtil.getNumberFormat(bedwarsdoubles[1]),
                    " §8▪ §7Финальных убийств: §e" + StringUtil.getNumberFormat(bedwarsdoubles[2]),
                    " §8▪ §7Убийств: §e" + StringUtil.getNumberFormat(bedwarsdoubles[4]),
                    " §8▪ §7Сломано кроватей: §e" + StringUtil.getNumberFormat(bedwarsdoubles[3]),
                    "§fКомандный режим:",
                    " §8▪ §7Сыграно игр: §e" + StringUtil.getNumberFormat(bedwarsteam[0]),
                    " §8▪ §7Побед: §e" + StringUtil.getNumberFormat(bedwarsteam[1]),
                    " §8▪ §7Финальных убийств: §e" + StringUtil.getNumberFormat(bedwarsteam[2]),
                    " §8▪ §7Убийств: §e" + StringUtil.getNumberFormat(bedwarsteam[4]),
                    " §8▪ §7Сломано кроватей: §e" + StringUtil.getNumberFormat(bedwarsteam[3])
            ))));
        } else {
            inventory.setItem(13, new DItem(ItemUtil.setItemMeta(new ItemStack(Material.BED), "§bBedWars", Collections.singletonList("§cОшибка загрузки данных..."))));
        }

        if (eggwarsteam != null && eggwarsdoubles != null && eggwarssolo != null){
            inventory.setItem(14, new DItem(ItemUtil.setItemMeta(new ItemStack(Material.DRAGON_EGG), "§bEggWars", Arrays.asList(
                    "§fОдиночный режим:",
                    " §8▪ §7Сыграно игр: §e" + StringUtil.getNumberFormat(eggwarssolo[0]),
                    " §8▪ §7Побед: §e" + StringUtil.getNumberFormat(eggwarssolo[1]),
                    " §8▪ §7Финальных убийств: §e" + StringUtil.getNumberFormat(eggwarssolo[2]),
                    " §8▪ §7Убийств: §e" + StringUtil.getNumberFormat(eggwarssolo[4]),
                    " §8▪ §7Уничтожено яиц: §e" + StringUtil.getNumberFormat(eggwarssolo[3]),
                    " §8▪ §7Улучшено генераторов: §e" + StringUtil.getNumberFormat(eggwarssolo[5]),
                    "§fПарный режим:",
                    " §8▪ §7Сыграно игр: §e" + StringUtil.getNumberFormat(eggwarsdoubles[0]),
                    " §8▪ §7Побед: §e" + StringUtil.getNumberFormat(eggwarsdoubles[1]),
                    " §8▪ §7Финальных убийств: §e" + StringUtil.getNumberFormat(eggwarsdoubles[2]),
                    " §8▪ §7Убийств: §e" + StringUtil.getNumberFormat(eggwarsdoubles[4]),
                    " §8▪ §7Уничтожено яиц: §e" + StringUtil.getNumberFormat(eggwarsdoubles[3]),
                    " §8▪ §7Улучшено генераторов: §e" + StringUtil.getNumberFormat(eggwarsdoubles[5]),
                    "§fКомандный режим:",
                    " §8▪ §7Сыграно игр: §e" + StringUtil.getNumberFormat(eggwarsteam[0]),
                    " §8▪ §7Побед: §e" + StringUtil.getNumberFormat(eggwarsteam[1]),
                    " §8▪ §7Финальных убийств: §e" + StringUtil.getNumberFormat(eggwarsteam[2]),
                    " §8▪ §7Убийств: §e" + StringUtil.getNumberFormat(eggwarsteam[4]),
                    " §8▪ §7Уничтожено яиц: §e" + StringUtil.getNumberFormat(eggwarsteam[3]),
                    " §8▪ §7Улучшено генераторов: §e" + StringUtil.getNumberFormat(eggwarsteam[5])
            ))));
        } else {
            inventory.setItem(14, new DItem(ItemUtil.setItemMeta(new ItemStack(Material.DRAGON_EGG), "§bEggWars", Collections.singletonList("§cОшибка загрузки данных..."))));
        }

        inventory.setItem(40, new DItem(CustomItems.getBack(Language.RUSSIAN), //кнопка назад
                (clicker, clickType, slot) -> {
                    LastCraft.getSoundAPI().play(clicker, SoundType.PICKUP);
                    ProfileMainPage mainPage = LobbyAPI.getProfileGuiManager().getGui(ProfileMainPage.class, clicker);
                    if (mainPage == null) {
                        return;
                    }

                    mainPage.open();
                }));
    }
}
