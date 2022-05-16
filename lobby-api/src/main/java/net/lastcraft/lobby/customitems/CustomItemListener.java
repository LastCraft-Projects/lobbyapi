package net.lastcraft.lobby.customitems;

import net.lastcraft.api.event.gamer.GamerChangeLanguageEvent;
import net.lastcraft.api.event.gamer.GamerChangePrefixEvent;
import net.lastcraft.api.event.gamer.GamerChangeSkinEvent;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.locale.Language;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.lobby.Lobby;
import net.lastcraft.lobby.config.GameConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;

public class CustomItemListener extends DListener<Lobby> {

    private final GameConfig gameConfig;

    public CustomItemListener(Lobby lobby, GameConfig gameConfig) {
        super(lobby);
        this.gameConfig = gameConfig;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeLanguage(GamerChangeLanguageEvent e) {
        setItem(e.getGamer(), e.getLanguage());
    }

    private void setItem(BukkitGamer gamer, Language lang) {
        Player player = gamer.getPlayer();
        if (player == null) {
            return;
        }

        TypeCustomItem.SERVER_MENU.givePlayer(player, 0, lang);
        TypeCustomItem.GADGET_MENU.givePlayer(player, 4, lang);
        TypeCustomItem.SELECTOR_LOBBY.givePlayer(player, 8, lang);
        if (gameConfig != null && gameConfig.getShopNPC() != null) {
            TypeCustomItem.SHOP.givePlayer(player, 2, lang);
        }

        CustomItem.giveProfileItem(player, lang);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(e.getPlayer());
        if (gamer == null) {
            return;
        }

        setItem(gamer, gamer.getLanguage());
    }

    @EventHandler
    public void onChangePref(GamerChangePrefixEvent e) {
        Player player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }
        CustomItem.giveProfileItem(player);
    }

    @EventHandler
    public void onChangeSkin(GamerChangeSkinEvent e) {
        Player player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }
        CustomItem.giveProfileItem(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.CRAFTING) {
            return;
        }

        if (e.getClick() != org.bukkit.event.inventory.ClickType.NUMBER_KEY) {
            return;
        }

        e.setCancelled(true);
    }
}
