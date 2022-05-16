package net.lastcraft.lobby.api.profile;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.scoreboard.ScoreBoardAPI;
import net.lastcraft.base.locale.Language;

public interface BoardLobby {

    ScoreBoardAPI SCORE_BOARD_API = LastCraft.getScoreBoardAPI();

    void showBoard(BukkitGamer gamer, Language lang);
}
