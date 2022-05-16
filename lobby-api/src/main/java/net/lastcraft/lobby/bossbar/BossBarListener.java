package net.lastcraft.lobby.bossbar;

import net.lastcraft.api.event.gamer.GamerChangeLanguageEvent;
import net.lastcraft.api.event.gamer.async.AsyncGamerJoinEvent;
import net.lastcraft.api.event.gamer.async.AsyncGamerQuitEvent;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.lobby.Lobby;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class BossBarListener extends DListener<Lobby> {

    private final BossBarLobby barLobby;

    public BossBarListener(Lobby lobby) {
        super(lobby);

        barLobby = new BossBarLobby(lobby);
    }

    @EventHandler
    public void onJoin(AsyncGamerJoinEvent e) {
        BukkitGamer gamer = e.getGamer();

        BossBar bossBar = barLobby.get(gamer.getLanguage());
        if (bossBar != null) {
            bossBar.addPlayer(gamer.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(AsyncGamerQuitEvent e) {
        BukkitGamer gamer = e.getGamer();

        BossBar bossBar = barLobby.get(gamer.getLanguage());
        if (bossBar != null) {
            bossBar.removePlayer(gamer.getPlayer());
        }
    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        Player player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }

        BossBar bossBar = barLobby.get(e.getOldLanguage());
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }

        BukkitUtil.runTaskLaterAsync(5L, () -> {
            BossBar bar = barLobby.get(e.getLanguage());
            if (bar != null) {
                bar.addPlayer(player);
            }
        });
    }
}


