package net.lastcraft.lobby.game.npc;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.lastcraft.api.entity.npc.NPC;
import net.lastcraft.api.event.gamer.GamerChangeLanguageEvent;
import net.lastcraft.api.event.gamer.GamerInteractNPCEvent;
import net.lastcraft.api.event.gamer.async.AsyncGamerJoinEvent;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.lobby.Lobby;
import net.lastcraft.lobby.config.GameConfig;
import net.lastcraft.lobby.game.data.Channel;
import net.lastcraft.lobby.game.guis.GameGui;
import net.lastcraft.lobby.game.guis.SpectatorGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;
import java.util.Map;

public class NPCListener extends DListener<Lobby> {

    private final Map<NPC, LobbyNPC> npcs;

    private final TIntObjectMap<SpectatorGui> spectatorGuis = TCollections.synchronizedMap(new TIntObjectHashMap<>());

    public NPCListener(Lobby lobby, GameConfig gameConfig) {
        super(lobby);
        this.npcs = gameConfig.getAllNpc();

        Arrays.stream(Language.values()).forEach(lang -> {
            spectatorGuis.put(lang.getId(), new SpectatorGui(gameConfig, lang));
        });

        Bukkit.getScheduler().runTaskTimerAsynchronously(lobby, () -> {
            gameConfig.getChannels().values().forEach(channel -> {
                channel.update(lobby.getServerStreamer());
                channel.getGuis().valueCollection().forEach(GameGui::update);
            });
            spectatorGuis.valueCollection().forEach(GameGui::update);
        }, 0, 20 * 5);//каждые 5 сек вызываю и все обновляю
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoinPlayer(AsyncGamerJoinEvent e) {
        BukkitGamer gamer = e.getGamer();

        spawn(gamer, gamer.getLanguage());
    }

    @EventHandler
    public void onClickNPC(GamerInteractNPCEvent e) {
        BukkitGamer gamer = e.getGamer();
        if (Cooldown.hasOrAddCooldown(gamer, "toServer", 20)) {
            return;
        }
        NPC npc = e.getNpc();

        LobbyNPC lobbyNPC = npcs.get(npc);
        if (lobbyNPC == null) {
            return;
        }

        if (lobbyNPC instanceof StartGameNPC) {
            StartGameNPC startGameNPC = (StartGameNPC) lobbyNPC;

            Channel channel = startGameNPC.getChannel();
            if (e.getAction() == GamerInteractNPCEvent.Action.LEFT_CLICK) {
                channel.sendToBestServer(gamer);
            } else {
                channel.getChannelGui(gamer).open(gamer);
            }
        }

        if (lobbyNPC instanceof ShopNPC) {
            Player player = gamer.getPlayer();
            if (player != null) {
                player.chat("/shop");
            }
        }

        if (lobbyNPC instanceof SpectatorNpc) {
            Language language = gamer.getLanguage();
            SpectatorGui spectatorGui = spectatorGuis.get(language.getId());
            if (spectatorGui == null) {
                spectatorGui = spectatorGuis.get(Language.getDefault().getId());
            }

            spectatorGui.open(gamer);
        }
    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        BukkitGamer gamer = e.getGamer();
        Language oldLang = e.getOldLanguage();
        Language lang = e.getLanguage();

        BukkitUtil.runTaskAsync(() -> {
            npcs.values().forEach(lobbyNPC ->
                    lobbyNPC.getHologram(oldLang).removeTo(gamer));

            spawn(gamer, lang);
        });
    }

    private void spawn(BukkitGamer gamer, Language lang) {
        npcs.values().forEach(lobbyNPC ->
                lobbyNPC.getHologram(lang).showTo(gamer));
    }
}
