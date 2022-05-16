package net.lastcraft.lobby.api.leveling;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.locale.Language;

public abstract class LevelReward {

    /**
     * выдать награду
     * @param gamer - кому выдать
     */
    public abstract void giveReward(BukkitGamer gamer);

    /**
     * получить описание иконки
     * @param language - язык для которого получить
     * @return - описание
     */
    public abstract String getLore(Language language);

    /**
     * чем выше приоритет, тем выше в списке
     */
    public int getPriority() {
        return -1;
    }
}
