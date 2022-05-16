package net.lastcraft.hub;

import lombok.RequiredArgsConstructor;
import net.lastcraft.api.CoreAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.scoreboard.Board;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.gamer.constans.PurchaseType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.lobby.api.profile.BoardLobby;

@RequiredArgsConstructor
public final class HBoard implements BoardLobby {

    private final GamerManager gamerManager = LastCraft.getGamerManager();
    private final CoreAPI coreAPI = LastCraft.getCoreAPI();

    @Override
    public void showBoard(BukkitGamer gamer, Language lang) {
        Board board = SCORE_BOARD_API.createBoard();
        board.setDynamicDisplayName("LASTCRAFT");

        Group group = gamer.getGroup();
        String groupName = (lang == Language.RUSSIAN ? group.getName() : group.getNameEn());

        board.updater(() -> {
            board.setDynamicLine(10, lang.getMessage("BOARD_MONEY") + ": §b",
                    StringUtil.getNumberFormat(gamer.getMoney(PurchaseType.MYSTERY_DUST)));
            board.setDynamicLine(11, lang.getMessage("BOARD_GOLD") + ": §6",
                    StringUtil.getNumberFormat(gamer.getMoney(PurchaseType.GOLD)));
            board.setDynamicLine(5, lang.getMessage("BOARD_GLOBAL_ONLINE") + ": §a",
                    String.valueOf(coreAPI.getOnline("*")));
        });
        board.setLine(14, StringUtil.getLineCode(14));
        board.setLine(13, StringUtil.getLineCode(13) + "§f" + lang.getMessage("BOARD_GROUP")
                + ": " + groupName);
        board.setLine(12, StringUtil.getLineCode(12));
        board.setLine(9, StringUtil.getLineCode(9));
        board.setLine(8, StringUtil.getLineCode(8) + "§f" + lang.getMessage("BOARD_BUSTER") + ":");
        board.setLine(7, StringUtil.getLineCode(7) + " §7" + lang.getMessage("BOARD_NO_BUSTER"));
        board.setLine(6, StringUtil.getLineCode(6));
        board.setLine(5, StringUtil.getLineCode(5));
        board.setLine(4, StringUtil.getLineCode(4));
        board.setLine(3, StringUtil.getLineCode(3) + "§f" + lang.getMessage("BOARD_VK")
                + ": §7vk.com/lastcraft");
        board.setLine(2, StringUtil.getLineCode(2) + "§f" + lang.getMessage("BOARD_SHOP")
                + ": §7last-craft.com");

        board.setLine(1, StringUtil.getLineCode(1) + "§f" + lang.getMessage("BOARD_DISCORD")
                + ": §7vk.cc/7BsqTk");

        board.showTo(gamer);
    }
}
