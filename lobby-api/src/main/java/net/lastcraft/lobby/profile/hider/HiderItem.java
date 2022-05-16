package net.lastcraft.lobby.profile.hider;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.effect.ParticleAPI;
import net.lastcraft.api.effect.ParticleEffect;
import net.lastcraft.api.event.gamer.GamerChangeHiderStateEvent;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.usableitem.UsableAPI;
import net.lastcraft.api.usableitem.UsableItem;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.SoundType;
import net.lastcraft.base.gamer.constans.SettingsType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.base.util.TimeUtil;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class HiderItem {
    private static final ParticleAPI PARTICLE_API = LastCraft.getParticleAPI();
    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    private static final Random RANDOM = new Random();
    private static final UsableAPI USABLE_API = LastCraft.getUsableAPI();
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    private static final TIntObjectMap<HiderItem> items = new TIntObjectHashMap<>();

    private final UsableItem enableItem;
    private final UsableItem disableItem;

    HiderItem(Language lang) {
        enableItem = USABLE_API.createUsableItem(ItemUtil.getBuilder(Head.getHeadByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjEyZjc4N2M1NGRkODlkMTI2OThkZDE3YjU2NTEyOTRjZmI4MDE3ZDZhZDRkMjZlZTZhOTFjZjFkMGMxYzQifX19"))
                .setName(lang.getMessage( "HIDER_ENABLE_NAME"))
                .setLore(lang.getList("HIDER_ENABLE_LORE"))
                .build(), (player, clickType, block) -> { //тот что показывает всех
            if (Cooldown.hasCooldown(player.getName().toLowerCase(), "hider")) {
                player.sendMessage(lang.getMessage("HIDER_COOLDOWN",
                        TimeUtil.leftTime(lang, 1000 * Cooldown.getSecondCooldown(player.getName().toLowerCase(), "hider"))));
                SOUND_API.play(player, SoundType.NOTE_BASS);
                return;
            }
            SOUND_API.play(player, SoundType.POP);
            Cooldown.addCooldown(player.getName().toLowerCase(), "hider", 20 * 10);
            setSettings(player, false);
        });
        disableItem = USABLE_API.createUsableItem(ItemUtil.getBuilder(Head.getHeadByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JkNDdkZDdjMzMzNmU3NWE2NjM5MWNkZjljOTM1ZmFlY2E4Y2UzOGFlMjJhMWIyNzg5NWUzMGI0NTI0NWE4In19fQ=="))
                .setName(lang.getMessage("HIDER_DISABLE_NAME"))
                .setLore(lang.getList("HIDER_DISABLE_LORE"))
                .build(), (player, clickType, block) -> { //тот что скрывает всех
            if (Cooldown.hasCooldown(player.getName().toLowerCase(), "hider")) {
                player.sendMessage(lang.getMessage("HIDER_COOLDOWN",
                        TimeUtil.leftTime(lang, 1000 * Cooldown.getSecondCooldown(
                                player.getName().toLowerCase(), "hider"))));
                SOUND_API.play(player, SoundType.NOTE_BASS);
                return;
            }
            SOUND_API.play(player, SoundType.POP);
            Cooldown.addCooldown(player.getName().toLowerCase(), "hider", 20 * 10);
            setSettings(player, true);
        });
        items.put(lang.getId(), this);
    }


    static void giveToPlayer(Player player, Language lang, boolean enable) {
        HiderItem hiderItem = items.get(lang.getId());
        if (hiderItem == null) {
            hiderItem = items.get(Language.getDefault().getId());
        }

        player.getInventory().setItem(7, (enable ?
                hiderItem.enableItem.getItemStack() :
                hiderItem.disableItem.getItemStack()));
    }

    public static void setSettings(Player player, boolean result) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        GamerChangeHiderStateEvent event = new GamerChangeHiderStateEvent(gamer, result);
        BukkitUtil.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        giveToPlayer(player, gamer.getLanguage(), result);
        gamer.setSetting(SettingsType.HIDER, result);

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (otherPlayer == player) {
                continue;
            }

            BukkitGamer otherGamer = GAMER_MANAGER.getGamer(otherPlayer);
            if (otherGamer == null || gamer.getFriends().containsKey(otherGamer.getPlayerID()) || otherGamer.isJunior()) {
                continue;
            }

            effect(otherPlayer, player);

            if (result) {
                player.hidePlayer(otherPlayer);
            } else {
                player.showPlayer(otherPlayer);
            }
        }
    }

    private static void effect(Player player, Player owner) {
        for (int i = 0; i < 9; i++) {
            Location location = player.getLocation().clone()
                    .add(0.0, 1.0, 0.0)
                    .add(Math.random() * (RANDOM.nextInt() % 2 == 0 ? 1 : -1),
                            Math.random() * (RANDOM.nextInt() % 2 == 0 ? 1 : -1),
                            Math.random() * (RANDOM.nextInt() % 2 == 0 ? 1 : -1));
            PARTICLE_API.sendEffect(ParticleEffect.SPELL, location, 0.1f, 0.1f,
                    0.1f, 0.1f, 2, owner);
        }
    }
}
