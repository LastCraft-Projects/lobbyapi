package net.lastcraft.lobby.game.old.stats;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.locale.Localization;
import net.lastcraft.base.sql.GlobalLoader;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.base.util.StringUtil;
import org.bukkit.entity.Player;

@Deprecated
public class StatsCommand implements CommandInterface {

    public StatsCommand(){
        SpigotCommand spigotCommand = COMMANDS_API.register("stats", this);
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String command, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        int size = args.length;
        if (size == 0) {
            OldStatsGui gui = gamer.getData("OLD_STATS_GUI");
            if (gui == null) {
                gui = new OldStatsGui(gamer, gamer);
                gui.load();
                gamer.addData("OLD_STATS_GUI", gui);
            }

            gui.open(player);
            return;
        }
        int lang = gamerEntity.getLanguage().getId();

        if (Cooldown.hasCooldown(player.getName(), "stats_command")) {
            int time = Cooldown.getSecondCooldown(player.getName(), "stats_command");
            gamerEntity.sendMessage(String.format(Localization.getMessage(lang, "COOLDOWN"),
                    String.valueOf(time), StringUtil.getCorrectWord(time, "TIME_SECOND_1", lang)));
            return;
        }
        Cooldown.addCooldown(player.getName(), "stats_command", 25 * 20);

        if (size == 1) {
            if (!gamer.isMagma()) {
                gamerEntity.sendMessageLocale("NO_PERMS_GROUP", Group.MAGMA.getNameEn());
                return;
            }

            String name = args[0];
            int playerID = GlobalLoader.containsPlayerID(name);
            if (playerID == -1) {
                COMMANDS_API.playerNeverPlayed(gamerEntity, name);
                return;
            }

            OldStatsGui gui = new OldStatsGui(LastCraft.getGamerManager().getOrCreate(playerID), gamer);
            gui.load();
            gui.open(player);
            return;
        }

        COMMANDS_API.notEnoughArguments(gamerEntity, "STATS_FORMAT");
    }
}
