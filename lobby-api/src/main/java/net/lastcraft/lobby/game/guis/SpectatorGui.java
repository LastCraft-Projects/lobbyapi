package net.lastcraft.lobby.game.guis;

import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.util.InventoryUtil;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.SoundType;
import net.lastcraft.base.game.GameState;
import net.lastcraft.base.locale.Language;
import net.lastcraft.lobby.config.GameConfig;
import net.lastcraft.lobby.game.data.Channel;
import net.lastcraft.lobby.game.data.Server;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public final class SpectatorGui extends GameGui {

    private final List<Channel> channels;

    public SpectatorGui(GameConfig gameConfig, Language lang) {
        super("LOBBY_SPECTATE_GUI_NAME", lang);
        this.channels = new ArrayList<>(gameConfig.getChannels().values());
    }

    @Override
    protected void setItems() {
        List<Server> servers = new ArrayList<>();
        for (Channel channel : channels) {
            channel.getServers().values().forEach(server -> {
                if (!server.isAlive() || server.getGameState() != GameState.GAME) {
                    return;
                }

                servers.add(server);
            });
        }
        int pagesCount = InventoryUtil.getPagesCount(servers.size(), 21);

        multiInventory.clearInventories();
        INVENTORY_API.pageButton(lang, pagesCount, multiInventory.getInventories(), 38, 42);

        if (servers.isEmpty()) {
            multiInventory.setItem(22, new DItem(ItemUtil.getBuilder(Material.GLASS_BOTTLE)
                    .setName(lang.getMessage("LOBBY_SERVER_NOT_FOUND_NAME"))
                    .setLore(lang.getList("LOBBY_SERVER_NOT_FOUND_LORE"))
                    .build(), (player, clickType, clickSlot) -> SOUND_API.play(player, SoundType.TELEPORT)));
            return;
        }

        int slot = 10;
        int page = 0;
        for (Server server : servers) {
            multiInventory.setItem(page, slot++, new DItem(ItemUtil.getBuilder(Material.FIREBALL)
                    .setName("§c" + server.getName().toUpperCase())
                    .setAmount(server.getOnline() > 0 ? server.getOnline() : 1)
                    .setLore(lang.getList("LOBBY_SERVER_SPECTATE_LORE",
                            server.getMap(),
                            server.getOnline(),
                            server.getMaxPlayer()))
                    .build(), (player, clickType, i) -> CORE_API.sendToServer(player, server.getName())));

            if ((slot - 8) % 9 == 0)
                slot += 2;

            if (slot >= 35) {
                slot = 10;
                page++;
            }
        }
    }
}
