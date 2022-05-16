package net.lastcraft.lobby.profile.hider;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.event.gamer.GamerChangeLanguageEvent;
import net.lastcraft.api.event.gamer.GamerFriendEvent;
import net.lastcraft.api.event.gamer.async.AsyncGamerJoinEvent;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.friend.FriendAction;
import net.lastcraft.base.gamer.constans.SettingsType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;

public class HiderListener extends DListener<Lobby> {

    private final GamerManager gamerManager = LastCraft.getGamerManager();

    public HiderListener(Lobby lobby) {
        super(lobby);
        Arrays.stream(Language.values()).forEach(HiderItem::new);
    }

    @EventHandler
    public void onJoin(AsyncGamerJoinEvent e) {
        BukkitGamer gamer = e.getGamer();
        Player player = gamer.getPlayer();

        if (player == null || !player.isOnline()) {
            return;
        }

        HiderItem.giveToPlayer(player, gamer.getLanguage(), gamer.getSetting(SettingsType.HIDER));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        BukkitGamer gamer = gamerManager.getGamer(player);
        if (gamer == null) {
            return;
        }

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (player == otherPlayer) {
                continue;
            }

            BukkitGamer otherGamer = gamerManager.getGamer(otherPlayer);
            if (otherGamer == null || gamer.getFriends().containsKey(otherGamer.getPlayerID())) {
                continue;
            }

            if (gamer.getSetting(SettingsType.HIDER) && !otherGamer.isJunior()) {
                player.hidePlayer(otherPlayer);
            }

            if (otherGamer.getSetting(SettingsType.HIDER) && !gamer.isJunior()) {
                otherPlayer.hidePlayer(player);
            }
        }
    }

    @EventHandler
    public void onFriend(GamerFriendEvent e) {
        BukkitGamer gamer = e.getGamer();

        BukkitGamer otherGamer = gamerManager.getGamer(e.getFriend().getPlayerId());
        if (otherGamer == null) {
            return;
        }

        Player otherPlayer = otherGamer.getPlayer();
        Player player = gamer.getPlayer();
        if (player == null || otherPlayer == null || !otherPlayer.isOnline() || !player.isOnline()) {
            return;
        }

        if (e.getAction() == FriendAction.ADD_FRIEND && gamer.getSetting(SettingsType.HIDER)) {
            player.showPlayer(otherPlayer);
        }

        if (e.getAction() == FriendAction.REMOVE_FRIEND
                && gamer.getSetting(SettingsType.HIDER)
                && !otherGamer.isJunior()) {
            player.hidePlayer(otherPlayer);
        }

    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        BukkitGamer gamer = e.getGamer();
        Player player = gamer.getPlayer();
        if (player == null) {
            return;
        }

        Language lang = e.getLanguage();

        HiderItem.giveToPlayer(player, lang, gamer.getSetting(SettingsType.HIDER));
    }
}
