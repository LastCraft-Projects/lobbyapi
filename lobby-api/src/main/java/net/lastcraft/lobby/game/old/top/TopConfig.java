package net.lastcraft.lobby.game.old.top;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.lastcraft.api.util.ConfigManager;
import net.lastcraft.dartaapi.utils.bukkit.LocationUtil;
import net.lastcraft.dartaapi.utils.core.CoreUtil;
import net.lastcraft.lobby.game.old.top.data.TopData;
import net.lastcraft.lobby.game.old.top.data.TopTask;
import net.lastcraft.lobby.game.old.top.sql.PlayerSelectedLoader;
import net.lastcraft.lobby.game.old.top.sql.TopLoader;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@UtilityClass
public class TopConfig {

    @Getter
    private TopLoader topLoader;

    @Getter
    private List<Location> locations;

    public void loadTOP(JavaPlugin javaPlugin){
        File file = new File(CoreUtil.getConfigDirectory() + "/oldTop.yml");
        if (!file.exists()) return;

        locations = new ArrayList<>();

        ConfigManager configManager = new ConfigManager(file);
        FileConfiguration config = configManager.getConfig();

        for (String stringLocation : config.getStringList("Locations")){
            Location location = LocationUtil.stringToLocation(stringLocation, true);
            locations.add(location);
        }

        String tableSelected = config.getString("Main");
        new PlayerSelectedLoader(tableSelected);

        for (String section : config.getConfigurationSection("Tables").getKeys(false)) {
            String patch = "Tables." + section + ".";
            int position = config.getInt(patch + "Position");
            String table = config.getString(patch + "Table");
            String type = config.getString(patch + "Type");
            new TopData(position, table, type);
        }

        topLoader = new TopLoader();
        new TopTask();
        new TopListener(javaPlugin);
    }
}
