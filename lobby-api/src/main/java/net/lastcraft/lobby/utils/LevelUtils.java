package net.lastcraft.lobby.utils;

import lombok.experimental.UtilityClass;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.scoreboard.DisplaySlot;
import net.lastcraft.api.scoreboard.Objective;
import net.lastcraft.api.scoreboard.ScoreBoardAPI;
import net.lastcraft.base.gamer.sections.NetworkingSection;
import org.bukkit.entity.Player;

@UtilityClass
public class LevelUtils {

    private final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private final ScoreBoardAPI SCORE_BOARD_API = LastCraft.getScoreBoardAPI();

    private Objective objectives;

    //перерасчитывать уровень игрока, опыт и лвл, когда он получает опыт (в баре и в обджективе)
    //выдавать опыт вместе с другими предметами из кейсов (от редкости зависит)
    //поменять сообщение при выдаче награды из кейса
    public void setExpData(BukkitGamer gamer) {
        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        if (objectives == null) {
            registerObjectives();
        }

        objectives.setScore(player, gamer.getLevelNetwork());
        player.setExp((float) NetworkingSection.getCurrentXPLVL(gamer.getExp()) /
                (float) NetworkingSection.checkXPLVL(gamer.getLevelNetwork() + 1));
        player.setLevel(gamer.getLevelNetwork());
    }

    private void registerObjectives() {
        objectives = SCORE_BOARD_API.createObjective("level", "dummy");
        objectives.setDisplayName("§e✫");
        objectives.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objectives.setPublic(true);
    }

}
