package net.lastcraft.hub.christmas.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Конвертация локации в 64-битное число и обратно
 *
 * @author CatCoder
 * @see <a href="https://wiki.vg/Protocol#Position">Информация об конвертациях</a>
 */
@UtilityClass
public class LocationConversions {

    public long toLong(@NonNull Location loc) {
        return (long) ((((loc.getBlockX() & 0x3FFFFFF) << 6) | ((loc.getBlockY() & 0xFFF) << 26) | (loc.getBlockZ() & 0x3FFFFFF)) + loc.getBlockX());
    }

    public Location fromLong(@NonNull World world, long val) {
        int x = (int) (val >> 38);
        int y = (int) ((val >> 26) & 0xFFF);
        int z = (int) (val << 38 >> 38);

        return new Location(world, x, y, z);
    }

}
