package net.lastcraft.rewards.managers;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.event.gamer.GamerChangeLanguageEvent;
import net.lastcraft.api.event.gamer.GamerInteractNPCEvent;
import net.lastcraft.api.event.gamer.async.AsyncGamerJoinEvent;
import net.lastcraft.api.hologram.Hologram;
import net.lastcraft.api.hologram.HologramAPI;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.locale.Language;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.rewards.Rewards;
import net.lastcraft.rewards.data.RewardGui;
import net.lastcraft.rewards.data.RewardPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class RewardListener extends DListener<Rewards> {

    private final ScheduledExecutorService updater = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, RewardGui> guiMap = new ConcurrentHashMap<>();
    private final TIntObjectMap<Hologram> base_holograms = new TIntObjectHashMap<>();

    public RewardListener(Rewards rewards) {
        super(rewards);

        //Gui updater
        updater.scheduleAtFixedRate(() -> {
            try {
                for (RewardGui gui : guiMap.values()) {
                    gui.update();
                }
            } catch (Exception e) {
                System.out.println("Exception with Rewards >> " + e.getMessage());
            }
        }, 1, 1, TimeUnit.SECONDS);

        HologramAPI hologramAPI = LastCraft.getHologramAPI();
        for (Language language : Language.values()) {
            Hologram rewardHologram = hologramAPI.createHologram(rewards.getHumanNPC()
                    .getLocation().add(0, 1.6, 0));
            rewardHologram.addTextLine(language.getList("REWARD_HOLOGRAM"));
            base_holograms.put(language.getId(), rewardHologram);
        }
    }

    private RewardGui getGui(Player player) {
        return guiMap.computeIfAbsent(player.getName().toLowerCase(), k -> new RewardGui(player, javaPlugin));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoad(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;

        new RewardPlayer(e.getName(), javaPlugin);
    }

    @EventHandler
    public void onJoin(AsyncGamerJoinEvent e) {
        BukkitGamer gamer = e.getGamer();

        RewardPlayer rewardPlayer = RewardPlayer.getPlayer(gamer.getName());
        if (rewardPlayer == null)
            return;

        Player player = gamer.getPlayer();
        if (player == null) {
            return;
        }

        rewardPlayer.createHologram(player, javaPlugin);
        rewardPlayer.showHologram(player);

        Hologram hologram = base_holograms.get(gamer.getLanguage().getId());
        if (hologram != null)
            hologram.showTo(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        saveData(event.getPlayer());
    }

    @EventHandler
    public void onChangeLanguage(GamerChangeLanguageEvent e) {
        Player bukkitPlayer = e.getGamer().getPlayer();
        if (bukkitPlayer == null) {
            return;
        }

        RewardPlayer player = RewardPlayer.getPlayer(bukkitPlayer.getName());
        if (player == null)
            return;

        player.createHologram(bukkitPlayer, javaPlugin);
        player.showHologram(bukkitPlayer);

        Language oldLang = e.getOldLanguage();
        Language lang = e.getLanguage();

        Hologram hologram = base_holograms.get(oldLang.getId());
        if (hologram != null)
            hologram.removeTo(bukkitPlayer);

        hologram = base_holograms.get(lang.getId());
        if (hologram != null)
            hologram.showTo(bukkitPlayer);

        RewardGui gui = getGui(bukkitPlayer);
        gui.createInventory();
    }

    @EventHandler
    public void onClick(GamerInteractNPCEvent e) {
        if (e.getNpc() != javaPlugin.getHumanNPC())
            return;

        BukkitGamer gamer = e.getGamer();

        if (gamer.getLevelNetwork() < 5) {
            gamer.sendMessageLocale("REWARDS_CANT_USE");
            return;
        }

        Player player = gamer.getPlayer();
        if (player == null) {
            return;
        }

        RewardGui gui = getGui(gamer.getPlayer());
        if (gui == null)
            return;

        gui.open();
    }

    private void saveData(Player player) {
        if (player == null)
            return;

        RewardPlayer rewardPlayer = RewardPlayer.getPlayer(player.getName());

        if (rewardPlayer != null) {
            rewardPlayer.removeHologram();
            RewardPlayer.removePlayer(player.getName());
        }

        guiMap.remove(player.getName().toLowerCase());
    }
}
