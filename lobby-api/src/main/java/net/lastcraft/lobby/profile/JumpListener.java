package net.lastcraft.lobby.profile;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.effect.ParticleAPI;
import net.lastcraft.api.effect.ParticleEffect;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.gamer.constans.SettingsType;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.lobby.Lobby;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JumpListener extends DListener<Lobby> {

    private final ParticleAPI particleAPI = LastCraft.getParticleAPI();
    private final Map<String, Boolean> cooldown = new HashMap<>();

    public JumpListener(Lobby javaPlugin) {
        super(javaPlugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        cooldown.remove(e.getPlayer().getName().toLowerCase());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = e.getPlayer();
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null || gamer.getGroup() == Group.DEFAULT || gamer.getSetting(SettingsType.FLY)) {
            return;
        }

        String name = player.getName().toLowerCase();
        if (cooldown.get(name) != null && cooldown.get(name)) {
            player.setAllowFlight(true);
        } else {
            player.setAllowFlight(false);
        }

        if (player.isOnGround()) {
            cooldown.put(name, true);
        }

        if (cooldown.get(name) != null && !cooldown.get(name)) {
            particleAPI.sendEffect(ParticleEffect.FLAME, getPlayers(gamer), player.getLocation());
        }
    }

    private List<Player> getPlayers(BukkitGamer owner) {
        List<Player> players = new ArrayList<>();
        for (BukkitGamer gamer : GAMER_MANAGER.getGamers().values()) {
            Player player = gamer.getPlayer();
            if (player == null || !player.isOnline()) {
                continue;
            }

            if (gamer == owner) {
                players.add(player);
                continue;
            }

            if (owner.isFriend(gamer)) {
                players.add(player);
                continue;
            }

            if (owner.isYouTube() || owner.isStaff()) {
                players.add(player);
                continue;
            }

            if (!gamer.getSetting(SettingsType.HIDER)) {
                players.add(player);
            }
        }

        return players;
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();
        String name = player.getName().toLowerCase();

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        if (cooldown.containsKey(name) && !gamer.getSetting(SettingsType.FLY)) {
            e.setCancelled(true);
            cooldown.put(name, false);

            player.setVelocity(player.getLocation().getDirection()
                    .multiply(1.6D)
                    .setY(1.0D));

            player.setAllowFlight(false);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        String name = player.getName().toLowerCase();

        if (!player.isOnGround() && cooldown.get(name) != null && !cooldown.get(name)) {
            player.setVelocity(new Vector(0, -5, 0));
        }
    }
}
