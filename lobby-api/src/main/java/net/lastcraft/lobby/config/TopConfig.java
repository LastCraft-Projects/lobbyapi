package net.lastcraft.lobby.config;

import lombok.Getter;
import net.lastcraft.api.util.LocationUtil;
import net.lastcraft.lobby.Lobby;
import net.lastcraft.lobby.game.top.TopManager;
import net.lastcraft.lobby.game.top.TopTable;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TopConfig extends LobbyConfig {

    private final List<Location> standLocations = new ArrayList<>();
    private final List<TopTable> topTables = new ArrayList<>();

    private TopManager topManager;

    private String table;

    private int time = 0;

    public TopConfig(Lobby lobby) {
        super(lobby, "top");
    }

    @Override
    public void load() {
        FileConfiguration config = getConfig();

        config.getStringList("Locations").forEach(stringLoc -> {
            Location location = LocationUtil.stringToLocation(stringLoc, true);
            standLocations.add(location);
        });
        
        table = config.getString("Main");
        time = config.getInt("Time");

        for (String table : config.getConfigurationSection("Tables").getKeys(false)) {
            String path = "Tables." + table + ".";
            String tableName = config.getString(path + "Table");
            String holoName = config.getString(path + "NameHolo");
            String columnName = config.getString(path + "Column", "Wins");
            String columnFormatKey = config.getString(path + "ColumnKey", "WINS_1");

            topTables.add(new TopTable(tableName, columnName, columnFormatKey, holoName, 0));
            topTables.add(new TopTable(tableName, columnName, columnFormatKey, holoName, 1));
        }
    }

    @Override
    public void init() {
        if (topManager != null || table == null || time == 0)
            return;

        topManager = new TopManager(this, table);
    }
}
