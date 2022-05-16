package net.lastcraft.lobby.game.old.stats;

import lombok.experimental.UtilityClass;
import net.lastcraft.dartaapi.loader.StatsLoader;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
@Deprecated
public class StatsUtil {

    private static final StatsLoader STATS_LOADER = new StatsLoader();

    public Map<String, int[]> loadStats(int playerID) {
        Map<String, int[]> stats = new HashMap<>();
        stats.put("BWT", loadStatsTable(playerID, "BedWarsTeam"));
        stats.put("BWD", loadStatsTable(playerID, "BedWarsDoubles"));
        stats.put("BWS", loadStatsTable(playerID, "BedWarsSolo"));
        stats.put("EWS", loadStatsTable(playerID, "EggWarsSolo"));
        stats.put("EWD", loadStatsTable(playerID, "EggWarsDoubles"));
        stats.put("EWT", loadStatsTable(playerID, "EggWarsTeam"));
        stats.put("KWS", loadStatsTable(playerID, "KitWarsSolo"));
        stats.put("KWT", loadStatsTable(playerID, "KitWarsTeam"));
        stats.put("LWS", loadStatsTable(playerID, "LuckyWarsSolo"));
        stats.put("LWT", loadStatsTable(playerID, "LuckyWarsTeam"));
        return stats;
    }

    public int[] loadStatsTable(int playerID, String table) {
        return STATS_LOADER.getStats(table, playerID);
    }
}
