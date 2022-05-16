package net.lastcraft.lobby.customitems;

import lombok.AllArgsConstructor;
import net.lastcraft.api.usableitem.ClickType;
import net.lastcraft.api.util.Head;
import net.lastcraft.base.locale.Language;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@AllArgsConstructor
public enum TypeCustomItem {
    SHOP(new CustomItem(Head.SHOP,
            "ITEMS_LOBBY_SHOP_NAME",
            "ITEMS_LOBBY_SHOP_LORE", (player, clickType, block) -> {
                if (clickType == ClickType.RIGHT) {
                    player.chat("/shop");
                }
            })),
    FAST_START(new CustomItem(Head.FASTSTART,
            "ITEMS_LOBBY_FAST_NAME",
            "ITEMS_LOBBY_FAST_LORE",
            (player, clickType, block) -> {
                if (clickType == ClickType.RIGHT) {
                    player.chat("/start");
                }
            })),
    GADGET_MENU(new CustomItem(Head.GADGETS,
            "ITEMS_LOBBY_GADGETS_NAME",
            "ITEMS_LOBBY_GADGETS_LORE",
            (player, clickType, block) -> {
                if (clickType == ClickType.RIGHT) {
                    player.chat("/gadgets");
                }
            })),
    SELECTOR_LOBBY(new CustomItem(Material.BLAZE_POWDER,
            "ITEMS_LOBBY_SELECTORS_NAME",
            "ITEMS_LOBBY_SELECTORS_LORE",
            (player, clickType, block) -> {
                if (clickType == ClickType.RIGHT) {
                    player.chat("/selector");
                }
            })),
    SERVER_MENU(new CustomItem(Material.COMPASS,
            "ITEMS_LOBBY_SELECTOR_NAME",
            "ITEMS_LOBBY_SELECTOR_LORE",
            (player, clickType, block) -> {
                if (clickType == ClickType.RIGHT) {
                    player.chat("/gamemenu");
                }
            }))
    ;

    private final CustomItem customItem;

    public void givePlayer(Player player, int slot, Language lang) {
        customItem.givePlayer(player, slot, lang);
    }
}
