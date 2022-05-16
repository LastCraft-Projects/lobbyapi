package net.lastcraft.hub.listeners;

import net.lastcraft.dartaapi.listeners.DListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class HorseListener extends DListener<JavaPlugin> {

    public HorseListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onLeavehorse(VehicleExitEvent e) {
        Vehicle vehicle = e.getVehicle();
        if (!(vehicle instanceof Horse))
            return;

        if (vehicle.getMetadata("owner").isEmpty())
            return;
        vehicle.remove();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory() instanceof HorseInventory))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Horse))
            return;

        if (entity.getMetadata("owner").isEmpty())
            return;
        e.setCancelled(true);
    }
}
