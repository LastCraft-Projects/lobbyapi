package net.lastcraft.box.listener;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.depend.PacketObject;
import net.lastcraft.api.event.gamer.GamerChangeGroupEvent;
import net.lastcraft.api.event.gamer.GamerChangeLanguageEvent;
import net.lastcraft.api.event.gamer.async.AsyncGamerJoinEvent;
import net.lastcraft.api.event.gamer.async.AsyncGamerQuitEvent;
import net.lastcraft.api.hologram.Hologram;
import net.lastcraft.api.hologram.HologramAPI;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.scoreboard.PlayerTag;
import net.lastcraft.api.scoreboard.ScoreBoardAPI;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.Cooldown;
import net.lastcraft.box.BoxPlugin;
import net.lastcraft.box.api.BoxAPI;
import net.lastcraft.box.data.Box;
import net.lastcraft.box.data.BoxHoloReplacer;
import net.lastcraft.box.gui.PlayerBoxGui;
import net.lastcraft.box.type.GroupBox;
import net.lastcraft.box.util.BoxUtil;
import net.lastcraft.dartaapi.listeners.DListener;
import net.lastcraft.dartaapi.utils.core.MathUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoxListener extends DListener<BoxPlugin> {

    private final ScoreBoardAPI scoreBoardAPI = LastCraft.getScoreBoardAPI();
    private final GamerManager gamerManager = LastCraft.getGamerManager();
    private final HologramAPI hologramAPI = LastCraft.getHologramAPI();
    private final List<Box> boxes = BoxAPI.getBoxes();
    private final String dataName = "boxHolograms";

    public BoxListener(BoxPlugin lobby) {
        super(lobby);
    }

    @EventHandler
    public void onJoinAsync(AsyncGamerJoinEvent e) {
        BukkitGamer gamer = e.getGamer();

        Language lang = gamer.getLanguage();

        Map<String, Hologram> boxHolograms = new HashMap<>();
        for (Box box : boxes) {
            Location location = box.getLocation();
            Hologram hologram = hologramAPI.createHologram(location.clone().add(0, 0.75, 0));
            hologram.addAnimationLine(2, new BoxHoloReplacer(gamer));
            boxHolograms.put(location.toString(), hologram);
            showBoxHologram(gamer, box, hologram);

            if (box.isWork()) {
                continue;
            }

            Hologram boxHologram = box.getHologram(lang);
            boxHologram.showTo(gamer);
        }
        gamer.addData(dataName, boxHolograms);
    }

    private void showBoxHologram(BukkitGamer gamer, Box box, Hologram hologram) {
        if (box.isWork() || BoxUtil.getKeys(gamer) == 0) {
            return;
        }

        hologram.showTo(gamer.getPlayer());
    }

    @EventHandler
    public void onQuitPlayerAsync(AsyncGamerQuitEvent e) {
        BukkitGamer gamer = e.getGamer();
        GroupBox.CHANGED_GROUPS.remove(gamer.getName().toLowerCase());
        Map<String, Hologram> boxHolograms = gamer.getData(dataName);
        if (boxHolograms == null) {
            return;
        }

        boxHolograms.values().forEach(PacketObject::remove);
    }

    @EventHandler
    public void onChangeGroup(GamerChangeGroupEvent e) {
        PlayerTag playerTag = scoreBoardAPI.getActiveDefaultTag().get(e.getGamer().getName());
        if (playerTag == null) {
            return;
        }

        playerTag.setPrefix(e.getGroup().getPrefix());
        playerTag.sendToAll();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock().getType() != Material.ENDER_CHEST) {
            return;
        }

        Box box = getBoxByBlock(e.getClickedBlock());
        if (box == null) {
            return;
        }

        Player player = e.getPlayer();
        BukkitGamer gamer = gamerManager.getGamer(player);
        if (gamer == null || Cooldown.hasOrAddCooldown(gamer, "box", 20)) {
            return;
        }

        Language lang = gamer.getLanguage();

        if (box.getOwner() == player || box.isWork()) {
            player.sendMessage(box.getPrefix(lang) + lang.getMessage("BOX_WORK"));
            return;
        }

        PlayerBoxGui gui = new PlayerBoxGui(gamer, box);
        gui.open();
    }

    @EventHandler
    public void onChangeLang(GamerChangeLanguageEvent e) {
        Player player = e.getGamer().getPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }

        Language oldLang = e.getOldLanguage();
        Language lang = e.getLanguage();

        for (Box box : boxes) {
            if (box.isWork()) {
                continue;
            }

            Hologram oldHologram = box.getHologram(oldLang);
            Hologram hologram = box.getHologram(lang);
            oldHologram.removeTo(player);
            hologram.showTo(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        for (Box box : boxes) {
            if (!box.isWork()) {
                continue;
            }

            Player boxPlayer = box.getOwner();
            Player player = e.getPlayer();
            if (boxPlayer == player) {
                continue;
            }

            if (player.getLocation().distance(box.getBlock().getLocation()) <= 4) {
                Vector vector = MathUtil.getVector(box.getBlock().getLocation(), player.getLocation());
                vector.multiply(0.3);
                player.setVelocity(vector);
            }
        }
    }

    private Box getBoxByBlock(Block block) {
        for (Box box : boxes) {
            if (box.getBlock().equals(block)) {
                return box;
            }
        }

        return null;
    }
}
