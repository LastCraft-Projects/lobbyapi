package net.lastcraft.hub.christmas;

import com.google.common.collect.Iterators;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.effect.ParticleEffect;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.util.LocationUtil;
import net.lastcraft.base.util.Pair;
import net.lastcraft.hub.HubBoard;
import net.lastcraft.hub.christmas.listener.PlayerListener;
import net.lastcraft.hub.christmas.util.FireworkUtils;
import net.lastcraft.hub.christmas.util.LocationConversions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ChristmasThings {

    public static final ZoneId TIME_ZONE = ZoneId.of("Europe/Moscow");
    public static final LocalDateTime NEW_YEAR_DATE = LocalDate.now(TIME_ZONE)
            .plusYears(1) //2018 -> 2019
            .withDayOfMonth(1) //16 dec -> 1 jan
            .withMonth(1) //12 month -> 1 month
            .atStartOfDay(); //01.01.2019

    public static final long TICKS_PER_SECOND = 20;
    public static final long FIREWORK_LAUNCH_PERIOD = TICKS_PER_SECOND * 3; //5 sec

    private final HubBoard plugin;

    @Getter
    private RemoteLocationStore remoteLocationStore;

    private Iterator<Location> fireworkIterator;

    public static boolean supports() {
        LocalDateTime now = LocalDateTime.now(TIME_ZONE);

        Duration duration = Duration.between(now, NEW_YEAR_DATE);

        return duration.abs().toDays() <= 16 || now.getMonthValue() == 1;

    }

    public void register() {
        if (!LastCraft.isHub()) {
            return;
        }

        Pair<TLongList, TLongObjectMap<Location>> locations = transformLocations(parseLocations(plugin.getConfig(), "heads"));

        this.remoteLocationStore = new RemoteLocationStore(plugin, locations.getFirst(), locations.getSecond());

        //Запсук фейерверков в определенном промежутке времени на заданных локациях
        this.fireworkIterator = Iterators.cycle(parseLocations(plugin.getConfig(), "fireworks"));

        startTimer(FIREWORK_LAUNCH_PERIOD, () -> {
            Location location = fireworkIterator.next();

            Firework firework = location.getWorld().spawn(location, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();

            meta.addEffect(FireworkUtils.createDefaultFireworkEffect());
            meta.setPower(3);

        }, false);

        startTimer(15, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                BukkitGamer gamer = LastCraft.getGamerManager().getGamer(player);

                if (gamer == null) {
                    continue;
                }

                for (TLongIterator iterator = remoteLocationStore.getLocations().iterator(); iterator.hasNext(); ) {
                    long val = iterator.next();

                    TLongList locs = remoteLocationStore.cachedLocationsOf(gamer.getPlayerID(), TLongArrayList::new);

                    if (locs == null) {
                        continue;
                    }

                    if (!locs.contains(val)) {
                        Location location = remoteLocationStore.getCachedLoc(val).clone();

                        LastCraft.getParticleAPI().sendEffect(
                                ParticleEffect.HEART,
                                location.add(location.getX() > 0 ? 0.5D : 0.5D, 0.6D,
                                        location.getZ() > 0 ? -0.5D : 0.5D),
                                0.18f, 0.02f, 0.18f,
                                0.01f, 1, player);

                    }
                }
            }
        }, true);

        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(remoteLocationStore), plugin);
    }


    public BukkitTask startTimer(long period, Runnable task, boolean async) {
        return async ? plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, period, period) :
                plugin.getServer().getScheduler().runTaskTimer(plugin, task, period, period);
    }

    public Collection<Location> parseLocations(@NonNull ConfigurationSection section, @NonNull String path) {
        assert section.isList(path) : "Given path is not a list";

        return section.getStringList(path)
                .stream()
                .map(raw -> LocationUtil.stringToLocation(raw, false))
                .collect(Collectors.toSet());
    }

    public Pair<TLongList, TLongObjectMap<Location>> transformLocations(@NonNull Collection<Location> locations) {
        TLongList set = new TLongArrayList();
        TLongObjectMap<Location> map = new TLongObjectHashMap<>();

        for (Location location : locations) {
            long val = LocationConversions.toLong(location);
            set.add(val);
            map.put(val, location.clone());
        }

        return new Pair<>(set, map);
    }
}
