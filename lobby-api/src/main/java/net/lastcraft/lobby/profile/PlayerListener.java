package net.lastcraft.lobby.profile;

import net.lastcraft.api.event.gamer.GamerChangeLanguageEvent;
import net.lastcraft.api.event.gamer.async.AsyncGamerJoinEvent;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.gamer.constans.SettingsType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.lobby.Lobby;
import net.lastcraft.lobby.api.LobbyAPI;
import net.lastcraft.lobby.api.profile.BoardLobby;
import net.lastcraft.lobby.config.SettingConfig;
import net.lastcraft.lobby.utils.LevelUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.HashSet;

public class PlayerListener extends DListener<Lobby> {

    private final SettingConfig settingConfig;

    public PlayerListener(Lobby lobby, SettingConfig settingConfig) {
        super(lobby);
        this.settingConfig = settingConfig;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoinAsync(AsyncGamerJoinEvent e) {
        BukkitGamer gamer = e.getGamer();

        Language lang = gamer.getLanguage();
        LevelUtils.setExpData(gamer);
        gamer.sendTitle(lang.getMessage( "TITLE_JOIN_LOBBY"), lang.getMessage( "SUBTITLE_JOIN_LOBBY"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        BukkitUtil.runTaskLaterAsync(10L, () -> player.setCompassTarget(settingConfig.getSpawn()));

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        if (!gamer.getSetting(SettingsType.CHAT)) {
            gamer.sendMessagesLocale("CHAT_LOBBY_OFF");
        }

        if (gamer.isDiamond() && gamer.getSetting(SettingsType.FLY)) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        if (gamer.getSetting(SettingsType.BOARD)) {
            BoardLobby boardLobby = LobbyAPI.getBoardLobby();
            if (boardLobby == null) {
                return;
            }

            boardLobby.showBoard(gamer, gamer.getLanguage());
        }
    }

    @EventHandler
    public void onSetSpawn(PlayerSpawnLocationEvent e) {
        e.setSpawnLocation(settingConfig.getSpawn());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getLocation().getY() <= 0) {
            player.teleport(settingConfig.getSpawn());
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        if (player.getWorld().getName().equals("lobby")) {
            if (gamer.isDiamond() && !gamer.getSetting(SettingsType.FLY)) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        } else if (gamer.isDiamond() && !gamer.getSetting(SettingsType.FLY)) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        Language lang = e.getLanguage();
        BukkitGamer gamer = e.getGamer();

        if (Cooldown.hasOrAddCooldown(gamer, "cooldown", 5)) {
            return;
        }

        gamer.sendMessage(lang.getMessage("LANGUAGE_CHANGE", lang.getName()));
        BoardLobby boardLobby = LobbyAPI.getBoardLobby();
        if (boardLobby == null || !gamer.getSetting(SettingsType.BOARD)) {
            return;
        }

        boardLobby.showBoard(gamer, lang);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        if (!gamer.getSetting(SettingsType.CHAT)) {
            gamer.sendMessageLocale("LOBBY_DISABLED_MESSAGE_TO_CHAT");
            e.setCancelled(true);
            return;
        }

        boolean youTube = gamer.getGroup().getLevel() >= Group.YOUTUBE.getLevel();
        for (Player target : new HashSet<>(e.getRecipients())) {
            if (target == player) {
                continue;
            }

            BukkitGamer targetGamer = GAMER_MANAGER.getGamer(target);
            if (targetGamer == null || targetGamer.getSetting(SettingsType.CHAT) || youTube) {
                continue;
            }

            e.getRecipients().remove(target);
        }
    }
}
