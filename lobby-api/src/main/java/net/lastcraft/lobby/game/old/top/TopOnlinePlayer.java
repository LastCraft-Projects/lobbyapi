package net.lastcraft.lobby.game.old.top;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.hologram.Hologram;
import net.lastcraft.api.hologram.HologramAPI;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.locale.Localization;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import net.lastcraft.dartaapi.utils.bukkit.LocationUtil;
import net.lastcraft.lobby.game.old.top.data.TopData;
import net.lastcraft.lobby.game.old.top.sql.PlayerSelectedLoader;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class TopOnlinePlayer {

    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private static final HologramAPI HOLOGRAM_API = LastCraft.getHologramAPI();

    private int selected;
    private Map<Integer, ArmorStandTop> armorStandTops;
    private Hologram mainHolo;

    public TopOnlinePlayer(String name) {
        armorStandTops = new HashMap<>();
        this.selected = PlayerSelectedLoader.getSelected(name);
    }

    public Hologram getMainHolo() {
        return mainHolo;
    }

    public int getSelected() {
        return selected;
    }

    public void spawnTop(int mode, BukkitGamer gamer) { //1 - создать, 2 - пересоздать
        TopData selectedData = TopData.getTopDatas().get(selected);
        int lang = gamer.getLanguage().getId();
        int top = 1;
        for (Location location : TopConfig.getLocations()) {
            if (mode == 1) {
                ArmorStandTop armorStandTop = new ArmorStandTop(gamer, top, location, selectedData);
                armorStandTops.put(top, armorStandTop);
            }
            if (mode == 2)
                armorStandTops.get(top).create(selectedData);

            top++;
        }
        mainHolo = HOLOGRAM_API.createHologram(LocationUtil.getCenter(TopConfig.getLocations()).subtract(0.0, 0.8, 1.0));
        mainHolo.addTextLine("§6§l" + selectedData.getTable());
        mainHolo.addTextLine(Localization.getList(lang, "HOLO_TOP_" + selectedData.getType().toUpperCase()));
        if (TopData.getTopDatas().size() > 1) {
            mainHolo.addTextLine(Localization.getMessage(lang, "HOLO_TOP_MAIN"));
        }
        mainHolo.showTo(gamer);
    }

    void removeData() {
        armorStandTops.values().forEach(ArmorStandTop::remove);
        armorStandTops.clear();

        if (mainHolo != null)
            mainHolo.remove();

    }

    void respawnTop(BukkitGamer gamer, int selected) {
        this.selected = selected;

        if (mainHolo != null) {
            mainHolo.remove();
        }

        spawnTop(2, gamer);
        BukkitUtil.runTaskAsync(() -> PlayerSelectedLoader.saveSelect(gamer, selected));
    }
}
