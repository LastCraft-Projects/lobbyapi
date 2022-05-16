package net.lastcraft.box.data;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.depend.PacketObject;
import net.lastcraft.api.effect.ParticleAPI;
import net.lastcraft.api.effect.ParticleEffect;
import net.lastcraft.api.hologram.Hologram;
import net.lastcraft.api.hologram.HologramAPI;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.base.SoundType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.box.api.ItemBox;
import net.lastcraft.box.util.BoxUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Box {
    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    private static final HologramAPI HOLOGRAM_API = LastCraft.getHologramAPI();
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private static final Random RANDOM = new Random();
    private static final ParticleAPI PARTICLE_API = LastCraft.getParticleAPI();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    @Getter
    private final Block block;
    private final Location location;
    private final TIntObjectMap<Hologram> holograms = TCollections.synchronizedMap(new TIntObjectHashMap<>());

    @Getter
    private final Set<String> playersOpenGui = new ConcurrentSet<>();

    @Getter
    private Player owner; //кто крутит кейс
    @Getter
    private boolean work; //крутится ли он сейчас или нет

    public Box(Location location) {
        this.block = location.getBlock();
        this.location = block.getLocation().clone().add(0.5, 0.5, 0.5);
        this.location.setYaw(0.0f);
        this.location.setPitch(0.0f);

        for (Language language : Language.values()) {
            Hologram hologram = HOLOGRAM_API.createHologram(getLocation().add(0, 0.3, 0));
            hologram.addTextLine(getName(language));
            hologram.addTextLine(language.getMessage("BOX_SUB_NAME"));
            holograms.put(language.getId(), hologram);
        }
    }

    private String getName(Language lang) {
        return "§b" + lang.getMessage( "BOX_NAME");
    }

    public Location getLocation(){
        return location.clone();
    }

    public String getPrefix(Language lang) {
        return getName(lang) + " §8| §f";
    }

    public Hologram getHologram(Language lang) {
        Hologram hologram = holograms.get(lang.getId());
        if (hologram == null) {
            hologram = holograms.get(Language.getDefault().getId());
        }

        return hologram;
    }

    public void onStart(BukkitGamer gamer, List<ItemBox> items, ItemBox winItem, ItemStack chestItem) {
        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        for (String name : playersOpenGui) {
            Player other = Bukkit.getPlayer(name);
            if (other != null) {
                other.closeInventory();
            }
        }
        playersOpenGui.clear();

        this.owner = player;
        work = true;
        EXECUTOR_SERVICE.execute(() -> {
            try {
                BoxUtil.enableBoxHologram(this, false);

                holograms.valueCollection().forEach(PacketObject::hideAll);
                Thread.sleep(200);
                BoxUtil.playChestAnimation(block, 1);
                Thread.sleep(800);

                Location location = this.location.clone();
                BlockFace face = ((Directional)block.getState().getData()).getFacing();
                location.setYaw(BoxUtil.directionToYaw(face));
                location.setPitch(BoxUtil.directionToYaw(face));

                Hologram chestHologram = HOLOGRAM_API.createHologram(location);
                chestHologram.addBigItemLine(false, chestItem); //можно менять маленький сундук в зависимости от ключа
                chestHologram.setPublic(true);
                location.getWorld().playSound(location, SOUND_API.getSound(SoundType.FIREWORK_LAUNCH), 1.0F, 1.0F);
                Thread.sleep(200);
                double startR = 2.0, radiusRate = 0.007, yRate = 0.005;
                final int positions = 100, firstIterations = 20;
                int nowPosition = (positions / 2);
                List<Location> circlePositions = BoxUtil.getCircleSide(location, startR);
                org.bukkit.util.Vector dist = circlePositions.get(nowPosition).subtract(chestHologram.getLocation()).toVector();
                for (int i = 1; i <= firstIterations; ++i) {
                    Location current = location.clone().add(dist.clone().multiply(((double)i) / firstIterations));
                    current.setY(location.getY());
                    chestHologram.onTeleport(current);
                    Thread.sleep(15);
                }
                double nowY = location.getY();
                while (startR >= 0.05) {
                    circlePositions = BoxUtil.getCircleSide(location, startR);
                    Location now = circlePositions.get((nowPosition++) % positions);
                    now.setY(nowY);
                    chestHologram.onTeleport(now);
                    PARTICLE_API.sendEffect(ParticleEffect.FIREWORKS_SPARK, chestHologram.getLocation().clone()
                            .add(0, 0.8, 0), 0.01F, 1);
                    nowY += yRate;
                    startR -= radiusRate;
                    Thread.sleep(7);
                }

                Location center = location.clone();
                center.setY(nowY);
                chestHologram.onTeleport(center);
                Thread.sleep(600);
                Location winHoloLocation = chestHologram.getLocation();
                chestHologram.remove();
                PARTICLE_API.sendEffect(ParticleEffect.EXPLOSION_NORMAL, winHoloLocation.clone()
                        .add(0, 0.4, 0), 0.05F, 25);
                location.getWorld().playSound(location, SOUND_API.getSound(SoundType.FIREWORK_BLAST2), 1.0F, 1.0F);

                Collections.shuffle(items);
                final int timesScroll = RANDOM.nextInt(6) + 3;
                for (int i = 0; i < timesScroll; ++i) {
                    location.getWorld().playSound(location, SOUND_API.getSound(SoundType.CLICK), 1.0F, 1.0F);
                    Vector to = winHoloLocation.clone().subtract(location).toVector();

                    List<Hologram> itemHologramList = new ArrayList<>();
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        BukkitGamer plGamer = GAMER_MANAGER.getGamer(pl);

                        if (plGamer == null) {
                            continue;
                        }

                        Language langGamer = plGamer.getLanguage();

                        Hologram itemHolo = HOLOGRAM_API.createHologram(location.clone().add(0, 0.7, 0));
                        itemHolo.addTextLine(items.get(i % items.size()).getName(langGamer));
                        itemHolo.addDropLine(false, items.get(i % items.size()).getIcon());
                        itemHolo.showTo(pl);

                        itemHologramList.add(itemHolo);
                    }

                    for (int j = 0; j < 10; ++j) {
                        Location spawnLocation = location.clone().add(0, 0.7, 0)
                                .clone().add(to.clone().multiply((j + 1.0) / 10.0));
                        itemHologramList.forEach(hologram -> hologram.onTeleport(spawnLocation));
                        Thread.sleep(25);
                    }
                    Thread.sleep(250);
                    itemHologramList.forEach(PacketObject::remove);
                }

                List<Hologram> winHolo = new ArrayList<>();
                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                    BukkitGamer otherGamer = GAMER_MANAGER.getGamer(otherPlayer);

                    if (otherGamer == null)
                        continue;

                    Language otherLang = otherGamer.getLanguage();

                    Hologram holo = HOLOGRAM_API.createHologram(winHoloLocation.clone().add(0, 0.5, 0));
                    holo.addTextLine(winItem.getName(otherLang));
                    holo.addDropLine(false, winItem.getIcon());
                    holo.showTo(otherPlayer);
                    winHolo.add(holo);

                    if (otherPlayer.equals(player)){
                        if (!player.isOnline()) {
                            continue;
                        }
                        player.sendMessage(getPrefix(otherLang) + otherLang.getMessage( "BOX_WINNER",
                                winItem.getRarity().getName(otherLang), winItem.getName(otherLang)));
                        winItem.onMessage(gamer);
                        continue;
                    }

                    otherPlayer.sendMessage(getPrefix(otherLang) + otherLang.getMessage( "BOX_WINNER_OTHER",
                            player.getDisplayName(), winItem.getRarity().getName(otherLang),
                            winItem.getName(otherLang)));
                }

                ParticleEffect effect = winItem.getRarity().getEffect();
                if (effect != null) {
                    PARTICLE_API.sendEffect(effect, getLocation().add(0, 1.7, 0), 0.0F, 0.0F,
                            0.0F, 0.05F, 30, 128);
                }

                location.getWorld().playSound(location, SOUND_API.getSound(SoundType.LEVEL_UP), 1.0F, 1.0F);
                Thread.sleep(2000);
                winHolo.forEach(holo -> holo.onTeleport(location));
                Thread.sleep(5 * 50);
                winHolo.forEach(PacketObject::remove);
                BoxUtil.playChestAnimation(block, 0);
                Thread.sleep(600);

                this.owner = null;
                work = false;
                showHolograms();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void showHolograms() {
        for (BukkitGamer gamer : GAMER_MANAGER.getGamers().values()) {
            Language lang = gamer.getLanguage();
            getHologram(lang).showTo(gamer);
        }

        BoxUtil.enableBoxHologram(this, true);
    }


    public void remove() {
        holograms.valueCollection().forEach(PacketObject::remove);
    }
}
