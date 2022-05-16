package net.lastcraft.rewards.bonuses;

import net.lastcraft.api.player.BukkitGamer;

import java.util.Random;

public interface Bonus {

    Random RANDOM = new Random();

    /**
     * Выдать награду игроку
     *
     * @param gamer - кому
     * @param type - тип награды
     * @param chat - писать ли в чат
     */
    void giveTo(BukkitGamer gamer, RewardType type, boolean chat);

}
