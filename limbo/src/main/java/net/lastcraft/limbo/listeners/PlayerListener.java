package net.lastcraft.limbo.listeners;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.lastcraft.base.locale.Language;
import net.lastcraft.limbo.Limbo;
import net.lastcraft.limbo.data.LimboItem;
import net.lastcraft.limbo.data.LimboPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PlayerListener implements Listener {

    private final Material hubItemType = Material.MAGMA_CREAM;
    private final int heldSlot = 4;

    private final TIntObjectMap<LimboItem> items = new TIntObjectHashMap<>();
    private final Map<String, LimboPlayer> players = new HashMap<>();

    private final Limbo limbo;

    public PlayerListener(Limbo limbo) {
        this.limbo = limbo;
        for (Language language : Language.values()) {
            items.put(language.getId(), new LimboItem(language, hubItemType));
        }
        Bukkit.getPluginManager().registerEvents(this, limbo);
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        String name = e.getName().toLowerCase();
        LimboPlayer limboPlayer = players.get(name);
        if (limboPlayer != null)
            return;

        players.put(name, new LimboPlayer(name));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        LimboPlayer limboPlayer = players.get(player.getName().toLowerCase());
        if (limboPlayer == null)
            return;

        LimboItem limboItem = items.get(limboPlayer.getPlayerID());
        if (limboItem == null)
            limboItem = items.get(Language.getDefault().getId());

        player.getInventory().setHeldItemSlot(heldSlot);
        player.getInventory().setItem(heldSlot, limboItem.getItemStack());
    }

    @EventHandler
    public void changeHeldSlot(PlayerItemHeldEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        players.remove(e.getPlayer().getName().toLowerCase());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack itemHand = e.getItem();
        if (itemHand == null)
            return;

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        limbo.sendToHub(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null)
            return;

        if (!e.getClickedInventory().equals(player.getInventory()))
            return;

        if (e.getCurrentItem().getType() == Material.AIR)
            return;

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() != hubItemType)
            return;

        limbo.sendToHub(player);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        disableChat(e, e);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        disableChat(e, e);
    }

    private void disableChat(PlayerEvent e, Cancellable cancellable) {
        Player player = e.getPlayer();
        cancellable.setCancelled(true);
        LimboPlayer limboPlayer = players.get(player.getName().toLowerCase());
        if (limboPlayer == null)
            return;
        Language language = limboPlayer.getLanguage();
        player.sendMessage(language.getMessage("LIMBO_NO_CHAT"));
    }

}
