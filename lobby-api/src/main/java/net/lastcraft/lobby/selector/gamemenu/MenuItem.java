package net.lastcraft.lobby.selector.gamemenu;

import lombok.Getter;
import net.lastcraft.api.CoreAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.lobby.api.game.GameUpdateType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class MenuItem {

    private static final CoreAPI CORE_API = LastCraft.getCoreAPI();

    @Getter
    private final DItem dItem;
    private final List<String> lore = new ArrayList<>();
    private final Language lang;
    @Getter
    private final int slot;
    private final String channelOnline;

    private boolean check = true;

    MenuItem(Language lang, int slot, ItemStack itemStack, String server, String online,
             GameUpdateType gameUpdateType) {

        itemStack = ItemUtil.getBuilder(itemStack)
                .removeFlags()
                .build();

        this.slot = slot;
        this.lang = lang;
        this.channelOnline = (online == null ? server : online);

        ItemMeta meta = itemStack.getItemMeta();
        this.lore.addAll(meta.getLore());

        if (gameUpdateType != GameUpdateType.DEFAULT) {
            String name = meta.getDisplayName();
            itemStack = ItemUtil.getBuilder(itemStack)
                    .setName(name + " " + gameUpdateType.getChatColor() + lang.getMessage(gameUpdateType.getKey()))
                    .glowing()
                    .build();
        }

        dItem = new DItem(itemStack, (clicker, clickType, slot1) -> CORE_API.sendToServer(clicker, server));
    }

    MenuItem(Language lang, int slot, ItemStack itemStack, String server, String online) {
        this(lang, slot, itemStack, server, online, GameUpdateType.DEFAULT);
    }

    MenuItem(Language lang, int slot, ItemStack itemStack, String server) {
        this(lang, slot, itemStack, server, (String) null);
    }

    MenuItem(Language lang, int slot, ItemStack itemStack, String server, GameUpdateType gameUpdateType) {
        this(lang, slot, itemStack, server, null, gameUpdateType);
    }

    public void update() {
        int online = CORE_API.getOnline(channelOnline);

        dItem.setItem(ItemUtil.getBuilder(dItem.getItem())
                .setLore(lore)
                .addLore((online >= 0 ? lang.getMessage("GAMEMENU_ONLINE_LORE",
                        StringUtil.getNumberFormat(online),
                        StringUtil.getCorrectWord(online, "PLAYERS_1", lang))
                        : lang.getMessage("HOLO_REPLACER_CHANNEL_ERROR")))
                .addLore(" ")
                .addLore((check ? "§e▶ " : "§e  ") + lang.getMessage( "GAMEMENU_CLICKCONNECT_LORE"))
                .build());

        this.check = !check;
    }
}
