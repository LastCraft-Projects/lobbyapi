package net.lastcraft.hub.christmas;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import gnu.trove.TCollections;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.lastcraft.base.redis.Redis;
import net.lastcraft.hub.christmas.util.LocationConversions;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Класс для работы с локациям голов и загрузка/сохранение локаций в БД
 *
 * @author CatCoder
 */
public class RemoteLocationStore {

    private static final String SALT = "christmas";
    private static final HashFunction HASH_FUNCTION = Hashing.md5();

    public static final long RECOVERY_MAGIC_NUMBER = 0x3FACCFF;

    @Getter
    private final Plugin plugin;

    private final TIntObjectMap<TLongList> cachedLocations = TCollections.synchronizedMap(new TIntObjectHashMap<>());
    @Getter
    private final TLongList locations;
    @Getter
    private final int maxHeadCount;

    private TLongObjectMap<Location> map;

    public RemoteLocationStore(Plugin plugin, TLongList locations, TLongObjectMap<Location> map) {
        this.plugin = plugin;
        this.locations = locations;
        this.maxHeadCount = 58;
        this.map = map;
    }

    /**
     * Рассчет хеша, который послужит ключем для БД (уникальным)
     *
     * @param playerId - ид игрока
     * @param salt     - соль (любая строка)
     * @return hash(playerId + salt)
     */
    public String calculateHash(int playerId, String salt) {
        return HASH_FUNCTION
                .newHasher()
                .putInt(playerId)
                .putString(salt, StandardCharsets.UTF_8)
                .hash()
                .toString();
    }

    public Location getCachedLoc(long val) {
        return map.get(val);
    }

    public int fixedSize(int size) {
        return size >= maxHeadCount ? maxHeadCount : size;
    }

    public TLongObjectMap<Location> getCachedLocMap() {
        return map;
    }

    ////////////////////////////////////////////////////////////////
    //
    //                  Работа с локальным кэшом
    //
    ////////////////////////////////////////////////////////////////


    public boolean locationExists(@NonNull Location location) {
        return locationExists(LocationConversions.toLong(location));
    }

    public boolean locationExists(long location) {
        return locations.contains(location);
    }

    public TLongList cachedLocationsOf(int playerId, Supplier<TLongList> supplier) {
        TLongList result = cachedLocations.get(playerId);

        if (result == null) {
            result = supplier.get();
            setCachedLocations(playerId, result);
        }

        return result;
    }


    public void setCachedLocations(int playerId, @NonNull TLongList cachedLocations) {
        this.cachedLocations.put(playerId, TCollections.synchronizedList(cachedLocations));
    }

    ////////////////////////////////////////////////////////////////
    //
    //                  Запросы к Редису
    //
    ////////////////////////////////////////////////////////////////

    public CompletableFuture<TLongList> listLocationsOf(int playerId) {
        return listLocationsOf(calculateHash(playerId, SALT));
    }

    public void putLocationAsync(int playerId, long location) {
        putLocationAsync(calculateHash(playerId, SALT), location);
    }

    public CompletableFuture<TLongList> listLocationsOf(String hashedKey) {
        CompletableFuture<TLongList> future = new CompletableFuture<>();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            TLongList result = TCollections.synchronizedList(new TLongArrayList());
            try (Jedis jedis = Redis.newJedisInstance()) {
                Redis.authenticateJedis(jedis);

                for (String value : jedis.smembers(hashedKey)) {
                    result.add(Long.parseLong(value));
                }
            }

            future.complete(result);
        });

        return future;
    }

    public void putLocationAsync(String hashedKey, long location) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = Redis.newJedisInstance()) {
                Redis.authenticateJedis(jedis);

                jedis.sadd(hashedKey, Long.toString(location));
            }
        });
    }
}
