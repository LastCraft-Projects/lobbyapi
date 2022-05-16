package net.lastcraft.rewards.utils;

import com.google.common.collect.Iterators;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.rewards.data.RewardPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * Created by Kambet on 29.05.2018
 */
public final class RewardsHologramReplacer implements Supplier<String> {

    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    private final BukkitGamer gamer;
    private final Iterator<String> colors;

    public RewardsHologramReplacer(Player player) {
        gamer = GAMER_MANAGER.getGamer(player);
        this.colors = Iterators.cycle(Arrays.asList("§d", "§6", "§e", "§a", "§b"));
    }

    @Override
    public String get() {
        if (gamer == null) {
            return "";
        }

        RewardPlayer player = RewardPlayer.getPlayer(gamer.getName());
        if (player == null) {
            return "";
        }

        Language lang = gamer.getLanguage();
        int amount = player.getAvailableCount();

        if (amount < 1) {
            return "";
        }

        return colors.next() + StringUtil.getCorrectWord(amount, "AVAILABLE_1", lang)
                + " " + StringUtil.getNumberFormat(amount)
                + " " + StringUtil.getCorrectWord(amount, "REWARDS_1", lang);
    }

}
