package net.lastcraft.lobby.customitems;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.usableitem.ClickAction;
import net.lastcraft.api.usableitem.ClickType;
import net.lastcraft.api.usableitem.UsableAPI;
import net.lastcraft.api.usableitem.UsableItem;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomItem {

    private static final UsableAPI USABLE_API = LastCraft.getUsableAPI();
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    private final TIntObjectMap<UsableItem> item = new TIntObjectHashMap<>();

    CustomItem(Head head, String keyName, String keyLore, ClickAction clickAction) {
        this(head.getHead(), keyName, keyLore, clickAction);
    }

    CustomItem(Material material, String keyName, String keyLore, ClickAction clickAction) {
        this(new ItemStack(material), keyName, keyLore, clickAction);
    }

    CustomItem(ItemStack itemStack, String keyName, String keyLore, ClickAction clickAction) {
        for (Language language : Language.values()) {
            item.put(language.getId(), USABLE_API.createUsableItem(ItemUtil.getBuilder(itemStack.clone())
                    .setName(language.getMessage(keyName))
                    .setLore(language.getList(keyLore))
                    .build(), clickAction));
        }
    }

    public void givePlayer(Player player, int slot, Language lang) {
        UsableItem usableItem = item.get(lang.getId());
        if (usableItem == null) {
            usableItem = item.get(Language.getDefault().getId());
        }

        player.getInventory().setItem(slot, usableItem.getItemStack());
    }

    public static void giveProfileItem(Player player) {
        giveProfileItem(player, null);
    }

    public static void giveProfileItem(Player player, Language lang) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        if (lang == null) {
            lang = gamer.getLanguage();
        }

        UsableItem usableItem = USABLE_API.createUsableItem(ItemUtil.getBuilder(gamer.getHead())
                .setName(lang.getMessage( "ITEMS_LOBBY_PROFILE_NAME") + " " + gamer.getChatName())
                .setLore(lang.getList("ITEMS_LOBBY_PROFILE_LORE"))
                .build(), (clicker, clickType, block) -> {

            if (clickType == ClickType.RIGHT) {
                clicker.chat("/profile");
            }

        });
        usableItem.setOwner(player);

        player.getInventory().setItem(1, usableItem.getItemStack());
    }
}
