package net.lastcraft.lobby.game.guis;

import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.types.SubType;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.InventoryUtil;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.SoundType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.lobby.game.data.Channel;
import net.lastcraft.lobby.game.data.ServersByMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class ChannelGui extends GameGui {

    private static final ItemStack NO_EMPTY = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
            .setDurability((short) 14)
            .build();

    private final Channel channel;

    public ChannelGui(Channel channel, Language lang) {
        super("LOBBY_CHANNEL_GUI_NAME", lang, channel.getSubType().getTypeName());
        this.channel = channel;
    }

    @Override
    protected void setItems() {
        Map<String, ServersByMap> serversByMap = channel.getServersByMap();

        multiInventory.clearInventories();
        int pagesCount = InventoryUtil.getPagesCount(serversByMap.size(), 17);
        INVENTORY_API.pageButton(lang, pagesCount, multiInventory.getInventories(), 38, 42);

        if (serversByMap.isEmpty()) {
            SubType subType = channel.getSubType();
            multiInventory.setItem(22, new DItem(ItemUtil.getBuilder(Material.BARRIER)
                    .setName("§c" + subType.getGameType().getName() + " " + subType.getTypeName())
                    .setLore(lang.getList("LOBBY_CHANNEL_GUI_NO_SERVERS"))
                    .build(), (player, clickType, clickSlot) -> SOUND_API.play(player, SoundType.TELEPORT)));
            return;
        }

        multiInventory.setItem(10, new DItem(ItemUtil.getBuilder(Material.EYE_OF_ENDER)
                .setName(lang.getMessage("LOBBY_CHANNEL_BEST_SERVER_NAME"))
                .setLore(lang.getList("LOBBY_CHANNEL_BEST_SERVER_LORE"))
                .build(), (player, clickType, i) -> {
            BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
            if (gamer == null) {
                return;
            }

            SOUND_API.play(player, SoundType.POP);
            channel.sendToBestServer(gamer);
        }));

        int pageNum = 0;
        int slot = 12;
        for (ServersByMap data : channel.getSortedServers()) {
            String map = data.getMap();
            boolean noEmpty = data.getEmpty() <= 0;

            multiInventory.setItem(pageNum, slot, new DItem(ItemUtil.getBuilder(noEmpty ?
                    NO_EMPTY.clone() : getItem(map))
                    .setName((noEmpty ? "§c" : "§a") + map)
                    .removeFlags()
                    .setLore(lang.getList("LOBBY_CHANNEL_MAP_LORE",
                            data.size(),
                            data.getEmpty()))
                    .build(), (player, clickType, i) -> {
                if (noEmpty) {
                    SOUND_API.play(player, SoundType.TELEPORT);
                    return;
                }

                BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
                if (gamer != null) {
                    channel.sendToBestServer(gamer, map);
                }
            }));

            if (slot == 16) {
                slot = 21;
            } else if (slot == 25) {
                slot = 28;
            } else if (slot == 34) {
                slot = 12;
                pageNum++;
            } else {
                slot++;
            }
        }
    }

    private static ItemStack getItem(String map) {
        if (map == null || map.length() == 0) {
            return new ItemStack(Material.MAP);
        }

        char first = map.charAt(0);
        return Head.valueOf(("LETTER_" + first).toUpperCase()).getHead();
    }
}
