package net.lastcraft.lobby.selector.gamemenu;

import lombok.Getter;
import net.lastcraft.api.CoreAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.type.DInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Language;
import net.lastcraft.lobby.api.game.GameUpdateType;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

@Getter
public final class GameMenu  {
    private static final InventoryAPI API = LastCraft.getInventoryAPI();
    private static final CoreAPI CORE_API = LastCraft.getCoreAPI();

    private final DInventory inventory;
    private final BukkitGamer gamer;
    private final Set<MenuItem> menuItems = new HashSet<>();

    public GameMenu(BukkitGamer gamer) { //для шалкеров!
        this.gamer = gamer;
        inventory = API.createInventory(gamer.getPlayer(), gamer.getLanguage().getMessage("GAMEMENU_NAME_GUI"), 5);
        setItems();
    }

    public void open() {
        inventory.openInventory(gamer);
    }

    public void updateItems() {
        for (MenuItem item : menuItems) {
            item.update();
        }

        menuItems.forEach(menuItem -> inventory.setItem(menuItem.getSlot(), menuItem.getDItem()));
    }

    private void setItems() {
        Language lang = gamer.getLanguage();
        ChatColor chatColor = ChatColor.GREEN;
        if (gamer.getGroup() == Group.ADMIN || gamer.getGroup() == Group.SHULKER) {
            chatColor = gamer.getPrefixColor();
        }
        menuItems.add(new MenuItem(lang, 9 + 3 - 1, ItemUtil.getBuilder(Material.ENDER_PEARL)
                .setName("§bSkyWars")
                .setLore(lang.getList("GAMEMENU_SKYWARS_LORE"))
                .build(), "@swlobby", "@sw", GameUpdateType.UPDATE));

        menuItems.add(new MenuItem(lang, 9 + 4 - 1, ItemUtil.getBuilder(Head.LUCKYWARS)
                .setName("§bLuckyWars")
                .setLore(lang.getList("GAMEMENU_LUCKYWARS_LORE"))
                .build(), "@lwlobby", "@lw"));

        menuItems.add(new MenuItem(lang, 9 + 5 - 1, ItemUtil.getBuilder(Material.BED)
                .setDurability(ItemUtil.getBedColor(chatColor))
                .setName("§bBedWars")
                .setLore(lang.getList("GAMEMENU_BEDWARS_LORE"))
                .build(), "@bwlobby", "@bw"));

        menuItems.add(new MenuItem(lang, 9 + 6 - 1, ItemUtil.getBuilder(Material.DRAGON_EGG)
                .setName("§bEggWars")
                .setLore(lang.getList("GAMEMENU_EGGWARS_LORE"))
                .build(), "@ewlobby", "@ew"));

        menuItems.add(new MenuItem(lang, 9 * 2 + 3 - 1, ItemUtil.getBuilder(Head.BATMAN)
                .setName( "§bKitWars")
                .setLore(lang.getList("GAMEMENU_KITWARS_LORE"))
                .build(), "@kwlobby", "@kw"));

        menuItems.add(new MenuItem(lang, 9 * 2 + 4 - 1, ItemUtil.getBuilder(Material.BLAZE_POWDER)
                .setName( "§bArcadeGames §c§lBETA!")
                .setLore(lang.getList("GAMEMENU_ARCADE_LORE"))
                .build(), "@arlobby", "@arcade", GameUpdateType.NEW));

        menuItems.add(new MenuItem(lang, 9 * 2 + 5 - 1, ItemUtil.getBuilder(Material.CHEST)
                .setName("§bSurvivalGames")
                .setLore(lang.getList( "GAMEMENU_SG_LORE"))
                .build(), "@sglobby", "@sg", GameUpdateType.NEW));

        menuItems.add(new MenuItem(lang, 9 * 2 + 6 - 1, ItemUtil.getBuilder(Material.LADDER)
                .setName("§bParkourRacers")
                .setLore(lang.getList("GAMEMENU_PARKOUR_LORE"))
                .build(), "@prlobby", "@pr", GameUpdateType.NEW));

        menuItems.add(new MenuItem(lang, 9 * 3 + 3 - 1, ItemUtil.getBuilder(Material.IRON_PICKAXE)
                .setName("§bAnarchy")
                .setLore(lang.getList("GAMEMENU_ANARCHY_LORE"))
                .build(), "anarchy", "@anarchy", GameUpdateType.WIPE));

        menuItems.add(new MenuItem(lang, 9 * 3 + 4 - 1, ItemUtil.getBuilder(Material.GRASS)
                .setDurability((short) 2)
                .setName("§bSkyBlock 2")
                .setLore(lang.getList("GAMEMENU_SB_LORE"))
                .build(), "skyblock2", GameUpdateType.WIPE));

        menuItems.add(new MenuItem(lang, 9 * 3 + 5 - 1, ItemUtil.getBuilder(Material.GRASS)
                .setName("§bSkyBlock")
                .setLore(lang.getList("GAMEMENU_SB_LORE"))
                .build(), "skyblock", GameUpdateType.WIPE));

        menuItems.add(new MenuItem(lang, 9 * 3 + 6 - 1, ItemUtil.getBuilder(Head.CREATIVE)
                .setName("§bCreative")
                .setLore(lang.getList("GAMEMENU_CREATIVE_LORE"))
                .build(), "creative"));

        updateItems();

        inventory.setItem(9 + 8 - 1, new DItem(ItemUtil.getBuilder(Material.ENCHANTMENT_TABLE)
                .setName("§b" + lang.getMessage( "PROFILE_MAIN_ITEM_DONATE_NAME"))
                .setLore(lang.getList( "PROFILE_MAIN_ITEM_DONATE_LORE"))
                .build(), (clicker, clickType, slot) -> clicker.chat("/donate")));

        inventory.setItem(9 * 3 + 8 - 1, new DItem(ItemUtil.getBuilder(Head.MAINLOBBY.getHead())
                .setName(lang.getMessage("GAMEMENU_LOBBY_NAME"))
                .setLore(lang.getList("GAMEMENU_LOBBY_LORE"))
                .build(), (clicker, clickType, slot) -> CORE_API.sendToServer(clicker, "@hub")));
    }


}
