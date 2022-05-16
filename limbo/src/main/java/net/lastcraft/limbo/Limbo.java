package net.lastcraft.limbo;

import lombok.Getter;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.connector.bukkit.BukkitConnector;
import net.lastcraft.limbo.listeners.LobbyGuardListener;
import net.lastcraft.limbo.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Limbo extends JavaPlugin {

    private Thread restart;

    @Getter
    private Location spawnLocation;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        spawnLocation = stringToLocation(getConfig().getString("spawn"));

        new LobbyGuardListener(this);

        if (!getConfig().getBoolean("login", true))
            new PlayerListener(this);

        runStopServer();
    }

    private Location stringToLocation(String loc) {
        String[] locSplit = loc.split(";");
        Location location = new Location(Bukkit.getWorld(locSplit[0]),
                Double.parseDouble(locSplit[1]),
                Double.parseDouble(locSplit[2]),
                Double.parseDouble(locSplit[3]));
        if (locSplit.length == 6) {
            location.setPitch(Float.parseFloat(locSplit[4]));
            location.setYaw(Float.parseFloat(locSplit[5]));
        }
        return location;
    }

    private void runStopServer() {
        restart = new Thread() {
            private final String time = getConfig().getString("timerestart");

            @Override
            public void run() {
                try {
                    while (true) {
                        if ((String.valueOf(time) + ":00").contains(getCurrentTimeStamp()))
                            System.exit(0);

                        Thread.sleep(1000L);
                    }
                }
                catch (InterruptedException ignored) {}
            }
        };
        restart.start();
    }

    private String getCurrentTimeStamp() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    @Override
    public void onDisable() {
        if (restart == null)
            return;

        restart.interrupt();
    }

    public void sendToHub(Player player) {
        if (Cooldown.hasCooldown(player.getName(), "redirect_players"))
            return;

        Cooldown.addCooldown(player.getName(), "redirect_players", 10L);

        BukkitConnector.getInstance().redirect(player, "@hub");
    }
}
