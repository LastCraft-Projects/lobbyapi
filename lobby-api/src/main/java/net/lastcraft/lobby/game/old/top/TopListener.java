package net.lastcraft.lobby.game.old.top;

import net.lastcraft.api.event.gamer.GamerChangeLanguageEvent;
import net.lastcraft.api.event.gamer.GamerInteractHologramEvent;
import net.lastcraft.api.event.gamer.async.AsyncGamerJoinEvent;
import net.lastcraft.api.event.gamer.async.AsyncGamerQuitEvent;
import net.lastcraft.api.hologram.Hologram;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.SoundType;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.lobby.game.old.top.data.TopData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class TopListener extends DListener<JavaPlugin> {

    private static final Map<String, TopOnlinePlayer> PLAYERS = new ConcurrentHashMap<>();

    public TopListener(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoadTopPlayer(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;

        String name = e.getName();
        PLAYERS.put(name, new TopOnlinePlayer(name));
    }

    @EventHandler
    public void onJoin(AsyncGamerJoinEvent e) {
        BukkitGamer gamer = e.getGamer();

        TopOnlinePlayer topOnlinePlayer = PLAYERS.get(gamer.getName());
        if (topOnlinePlayer == null) {
            return;
        }

        topOnlinePlayer.spawnTop(1, gamer);
    }

    @EventHandler
    public void onQuit(AsyncGamerQuitEvent e) {
        String name = e.getGamer().getName();

        TopOnlinePlayer topOnlinePlayer = PLAYERS.remove(name);
        if (topOnlinePlayer != null)
            topOnlinePlayer.removeData();

    }

    @EventHandler
    public void onChangeLanguage(GamerChangeLanguageEvent e) {
        BukkitGamer gamer = e.getGamer();
        TopOnlinePlayer topOnlinePlayer = PLAYERS.get(gamer.getName());
        topOnlinePlayer.respawnTop(gamer, topOnlinePlayer.getSelected());
    }

    @EventHandler
    public void onHoloClick(GamerInteractHologramEvent e) {
        BukkitGamer gamer = e.getGamer();
        Hologram hologram = e.getHologram();
        if (TopData.getTopDatas().size() < 2) {
            return;
        }

        TopOnlinePlayer topOnlinePlayer = PLAYERS.get(gamer.getName());

        if (topOnlinePlayer != null && hologram == topOnlinePlayer.getMainHolo()) {
            gamer.playSound(SoundType.CLICK);
            int next = TopData.getNext(topOnlinePlayer.getSelected());
            topOnlinePlayer.respawnTop(gamer, next);
        }
    }
}
