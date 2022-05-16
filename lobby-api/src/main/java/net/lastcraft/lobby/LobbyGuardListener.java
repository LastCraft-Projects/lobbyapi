package net.lastcraft.lobby;

import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.lobby.config.SettingConfig;
import net.lastcraft.packetlib.nms.NmsAPI;
import net.lastcraft.packetlib.nms.interfaces.NmsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class LobbyGuardListener extends DListener<Lobby> {

    private final NmsManager nmsManager = NmsAPI.getManager();
    private final SettingConfig settingConfig;

    public LobbyGuardListener(Lobby lobby, SettingConfig settingConfig) {
        super(lobby);
        Bukkit.getWorlds().forEach(world -> {
            world.setStorm(false);
            world.setAutoSave(false);
            world.setThundering(false);
        });
        this.settingConfig = settingConfig;
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e){
        e.setCancelled(true);
    }

    //@EventHandler(priority = EventPriority.LOWEST)
    //public void onSpawnMob(EntitySpawnEvent e) {
    //    e.setCancelled(true);
    //}

    @EventHandler
    public void onBreakPainting(HangingBreakByEntityEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInterract(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPhysic(BlockPhysicsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFrame(PlayerInteractEntityEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        e.setCancelled(true);
    }

    //@EventHandler
    //public void onItemSpawn(ItemSpawnEvent e) {
    //    e.setCancelled(true);
    //}

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Hanging) {
            e.setCancelled(true);
        }

        if (e.getEntity().getType() == EntityType.PLAYER) {
            Player player = (Player) e.getEntity();

            if (e.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                    e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                nmsManager.disableFire(player);
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (player.getLocation().getY() <= 0) {
            player.teleport(settingConfig.getSpawn());
        }

    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecayEvent(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockIgniteEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFade(BlockFadeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onForm(BlockFormEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        e.getWorld().setAutoSave(false);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorInterract(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(true);
    }
}
