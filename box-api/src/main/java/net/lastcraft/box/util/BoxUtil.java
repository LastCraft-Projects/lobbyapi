package net.lastcraft.box.util;

import lombok.experimental.UtilityClass;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.hologram.Hologram;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.util.Rarity;
import net.lastcraft.base.gamer.constans.KeyType;
import net.lastcraft.box.data.Box;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.packetlib.nms.NmsAPI;
import net.lastcraft.packetlib.nms.interfaces.NmsManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class BoxUtil {

    private final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private final NmsManager NMS_MANAGER = NmsAPI.getManager();
    private final BlockFace[] RADIAL = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.WEST,
            BlockFace.SOUTH_WEST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH,
            BlockFace.SOUTH_WEST};
    private final EnumMap<BlockFace, Integer> NOTCHES = new EnumMap<>(BlockFace.class);

    public int wrapAngle(int angle) {
        int wrappedAngle = angle;

        while (wrappedAngle <= -180) {
            wrappedAngle += 360;
        }

        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }

        return wrappedAngle;
    }

    public int getKeys(BukkitGamer gamer) {
        int keys = 0;
        for (KeyType keyType : KeyType.values()) {
            keys += gamer.getKeys(keyType);
        }
        return keys;
    }

    public int directionToYaw(BlockFace face) {
        return wrapAngle(45 * NOTCHES.get(face));
    }

    public List<Location> getCircleSide(Location center, double radius) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / 100;
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z, center.getYaw(), center.getPitch()));
        }
        return locations;
    }

    public void playChestAnimation(Block chest, int state) {
        BukkitUtil.runTaskAsync(() -> NMS_MANAGER.playChestAnimation(chest, state == 1));
    }

    public void enableBoxHologram(Box box, boolean enable) {
        if (box.isWork() && enable) {
            return;
        }

        Location location = box.getLocation();
        for (BukkitGamer gamer : GAMER_MANAGER.getGamers().values()) {
            if (!gamer.isOnline()) {
                continue;
            }

            Map<String, Hologram> boxHolograms = gamer.getData("boxHolograms");
            Player player = gamer.getPlayer();
            if (player == null || !player.isOnline() || boxHolograms == null) {
                continue;
            }

            Hologram hologram = boxHolograms.get(location.toString());
            if (hologram == null) {
                continue;
            }

            if (!enable) {
                hologram.removeTo(player);
                continue;
            }

            if (BoxUtil.getKeys(gamer) == 0) {
                continue;
            }

            hologram.showTo(player);
        }
    }

    /**
     * узнать ту сумму, которую получит игрок в зависимости от ключа и редкости
     * @param keyType - тип ключа
     * @param rarity - редкость
     * @return - сумма
     */
    public int getMoney(KeyType keyType, Rarity rarity) {
        int value = 0;
        switch (keyType) {
            case DEFAULT_KEY:
                value += 60;

                switch (rarity) {
                    case RARE:
                        value += 20;
                        break;
                    case EPIC:
                        value += 50;
                        break;
                    case LEGENDARY:
                        value += 100;
                        break;
                }

                break;
            case GAME_KEY:
                value += 100;

                switch (rarity) {
                    case RARE:
                        value += 30;
                        break;
                    case EPIC:
                        value += 80;
                        break;
                    case LEGENDARY:
                        value += 150;
                        break;
                }

                break;
            case GAME_COSMETIC_KEY:
                value += 500;

                switch (rarity) {
                    case RARE:
                        value += 400;
                        break;
                    case EPIC:
                        value += 900;
                        break;
                    case LEGENDARY:
                        value += 1500;
                        break;
                }

                break;
        }

        return value;
    }

    static {
        for (int i = 0; i < RADIAL.length; i++) {
            NOTCHES.put(RADIAL[i], i);
        }
    }
}
