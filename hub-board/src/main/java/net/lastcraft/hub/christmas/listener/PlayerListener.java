package net.lastcraft.hub.christmas.listener;

import gnu.trove.list.TLongList;
import lombok.RequiredArgsConstructor;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.event.gamer.async.AsyncGamerJoinEvent;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.constans.KeyType;
import net.lastcraft.base.gamer.constans.PurchaseType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.hub.christmas.RemoteLocationStore;
import net.lastcraft.hub.christmas.util.FireworkUtils;
import net.lastcraft.hub.christmas.util.LocationConversions;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final RemoteLocationStore locationStore;

    /**
     * Загрузка данных о найденных головах из БД.
     *
     * @param event - событие входа геймера
     */
    @EventHandler
    public void onAsyncLoad(AsyncGamerJoinEvent event) {
        BukkitGamer gamer = event.getGamer();

        locationStore
                .listLocationsOf(gamer.getPlayerID())
                .thenAccept(locations -> {
                    locationStore.setCachedLocations(gamer.getPlayerID(), locations);

                    TLongList locs = locationStore.cachedLocationsOf(gamer.getPlayerID(), null);

                    if (locs != null && locs.size() >= locationStore.getMaxHeadCount() && !locs.contains(RemoteLocationStore.RECOVERY_MAGIC_NUMBER)) {
                        locs.add(RemoteLocationStore.RECOVERY_MAGIC_NUMBER);

                        locationStore.putLocationAsync(gamer.getPlayerID(), RemoteLocationStore.RECOVERY_MAGIC_NUMBER);

                        reward(gamer, gamer.getLanguage());

                        System.out.println("Recovered " + gamer.getName() + "'s heads, locs=" + locations.size());
                    }
                });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        BukkitGamer gamer = LastCraft.getGamerManager().getGamer(event.getPlayer());

        if (gamer == null) {
            return;
        }

        String hash = locationStore.calculateHash(gamer.getPlayerID(), "christmas");

        if (gamer.isDeveloper()) {
            event.getPlayer().spigot().sendMessage(new ComponentBuilder(hash)
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, hash)).create());
        }
    }


    /**
     * Обработка взаимодействия с головой и сохранение найденных голов в БД
     *
     * @param event - событие взаимодействия с каким-либо блоком/предметом
     */
    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || event.getClickedBlock().getType() != Material.SKULL) {
            return;
        }
        BukkitGamer gamer = LastCraft.getGamerManager().getGamer(event.getPlayer());
        if (gamer == null) {
            return;
        }

        Language language = gamer.getLanguage();
        Location location = event.getClickedBlock().getLocation();
        long val = LocationConversions.toLong(location);

        if (!locationStore.locationExists(val)) {
            return;
        }


        TLongList locations = locationStore.cachedLocationsOf(gamer.getPlayerID(), null);

        if (locations.contains(val) || locations.contains(RemoteLocationStore.RECOVERY_MAGIC_NUMBER)) {
            return;
        }

        locations.add(val);

        locationStore.putLocationAsync(gamer.getPlayerID(), val);

        if (locations.size() < locationStore.getMaxHeadCount()) {
            gamer.sendTitle(
                    language.getMessage("HOLIDAY_HEAD_FOUND_TITLE"),
                    language.getMessage("HOLIDAY_HEAD_FOUND_SUBTITLE", locationStore.fixedSize(locations.size()), locationStore.getMaxHeadCount()));

            gamer.addExp(75);
            gamer.changeMoney(PurchaseType.MYSTERY_DUST, 100);

            gamer.sendActionBar("§fВы получили: §6+100 монет§f, §a+75 xp");

            LastCraft.getParticleAPI().launchInstantFirework(FireworkUtils.createDefaultFireworkEffect(), location);

        } else {
            reward(gamer, language);

            locations.add(RemoteLocationStore.RECOVERY_MAGIC_NUMBER);
            locationStore.putLocationAsync(gamer.getPlayerID(), RemoteLocationStore.RECOVERY_MAGIC_NUMBER);
        }

        event.setCancelled(true);
    }


    private void reward(BukkitGamer gamer, Language language) {
        gamer.sendTitle(
                language.getMessage("HOLIDAY_HEAD_FOUND_ALL_TITLE"),
                language.getMessage("HOLIDAY_HEAD_FOUND_ALL_SUBTITLE"));

        gamer.addExp(5000);
        gamer.changeMoney(PurchaseType.MYSTERY_DUST, 6000);
        gamer.changeKeys(KeyType.DEFAULT_KEY, 30);

        gamer.sendActionBar("§fВы получили: §6+6000 монет§f, §a+5000 xp§f, §a+30 ключей");

        gamer.playSound(Sound.ENTITY_PLAYER_LEVELUP);
    }
}
