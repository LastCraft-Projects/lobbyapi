package net.lastcraft.lobby.config;

import net.lastcraft.api.util.LocationUtil;
import net.lastcraft.api.util.Rarity;
import net.lastcraft.base.gamer.constans.KeyType;
import net.lastcraft.box.api.BoxAPI;
import net.lastcraft.box.api.ItemBoxManager;
import net.lastcraft.box.data.Box;
import net.lastcraft.box.type.KeysBox;
import net.lastcraft.box.type.MoneyBox;
import net.lastcraft.dartaapi.utils.core.RestartServer;
import net.lastcraft.lobby.Lobby;
import net.lastcraft.lobby.box.LobbyXpBox;
import net.lastcraft.lobby.commands.SpawnCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class SettingConfig extends LobbyConfig {

    private Location spawn;
    private String restartTime;

    public SettingConfig(Lobby lobby) {
        super(lobby, "settings");

        if (configManager != null) {
            return;
        }

        Bukkit.getPluginManager().disablePlugin(lobby); //офаем если нет дефолтного конфига
    }

    @Override
    public void load() {
        FileConfiguration config = configManager.getConfig();

        spawn = LocationUtil.stringToLocation(config.getString("Spawn"), true);

        if (config.contains("Boxes")) {
            config.getStringList("Boxes").forEach(stringLoc -> {
                Location location = LocationUtil.stringToLocation(stringLoc, false);
                BoxAPI.getBoxes().add(new Box(location));
            });
        }

        if (config.contains("Restart")) {
            this.restartTime = config.getString("Restart");
        }
    }

    @Override
    public void init() {
        if (spawn != null) {
            new SpawnCommand(this);
        }

        if (!BoxAPI.getBoxes().isEmpty()) {
            ItemBoxManager manager = BoxAPI.getItemBoxManager();
            manager.addItemBox(KeyType.DEFAULT_KEY, new MoneyBox(15, Rarity.COMMON));
            manager.addItemBox(KeyType.DEFAULT_KEY, new MoneyBox(25, Rarity.COMMON));
            manager.addItemBox(KeyType.DEFAULT_KEY, new MoneyBox(35, Rarity.COMMON));
            manager.addItemBox(KeyType.DEFAULT_KEY, new MoneyBox(100, Rarity.RARE));
            manager.addItemBox(KeyType.DEFAULT_KEY, new MoneyBox(200, Rarity.EPIC));
            manager.addItemBox(KeyType.DEFAULT_KEY, new MoneyBox(500, Rarity.LEGENDARY));
            manager.addItemBox(KeyType.DEFAULT_KEY, new LobbyXpBox(25, Rarity.COMMON));
            manager.addItemBox(KeyType.DEFAULT_KEY, new LobbyXpBox(35, Rarity.COMMON));
            manager.addItemBox(KeyType.DEFAULT_KEY, new LobbyXpBox(50, Rarity.COMMON));
            manager.addItemBox(KeyType.DEFAULT_KEY, new LobbyXpBox(100, Rarity.RARE));
            manager.addItemBox(KeyType.DEFAULT_KEY, new LobbyXpBox(200, Rarity.RARE));
            manager.addItemBox(KeyType.DEFAULT_KEY, new LobbyXpBox(500, Rarity.EPIC));
            manager.addItemBox(KeyType.DEFAULT_KEY, new LobbyXpBox(1500, Rarity.LEGENDARY));
            manager.addItemBox(KeyType.DEFAULT_KEY, new KeysBox(1, Rarity.RARE, KeyType.DEFAULT_KEY));
            manager.addItemBox(KeyType.DEFAULT_KEY, new KeysBox(3, Rarity.EPIC, KeyType.DEFAULT_KEY));
            manager.addItemBox(KeyType.DEFAULT_KEY, new KeysBox(5, Rarity.LEGENDARY, KeyType.DEFAULT_KEY));

            manager.addItemBox(KeyType.GAME_KEY, new MoneyBox(20, Rarity.COMMON));
            manager.addItemBox(KeyType.GAME_KEY, new MoneyBox(30, Rarity.COMMON));
            manager.addItemBox(KeyType.GAME_KEY, new MoneyBox(35, Rarity.COMMON));
            manager.addItemBox(KeyType.GAME_KEY, new MoneyBox(40, Rarity.COMMON));
            manager.addItemBox(KeyType.GAME_KEY, new MoneyBox(50, Rarity.COMMON));
            manager.addItemBox(KeyType.GAME_KEY, new MoneyBox(150, Rarity.RARE));
            manager.addItemBox(KeyType.GAME_KEY, new MoneyBox(250, Rarity.EPIC));
            manager.addItemBox(KeyType.GAME_KEY, new MoneyBox(800, Rarity.LEGENDARY));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(50, Rarity.COMMON));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(60, Rarity.COMMON));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(70, Rarity.COMMON));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(80, Rarity.COMMON));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(90, Rarity.COMMON));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(100, Rarity.COMMON));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(200, Rarity.RARE));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(230, Rarity.RARE));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(250, Rarity.RARE));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(700, Rarity.EPIC));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(900, Rarity.EPIC));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(1800, Rarity.LEGENDARY));
            manager.addItemBox(KeyType.GAME_KEY, new LobbyXpBox(1500, Rarity.LEGENDARY));
            manager.addItemBox(KeyType.GAME_KEY, new KeysBox(2, Rarity.RARE, KeyType.GAME_KEY));
            manager.addItemBox(KeyType.GAME_KEY, new KeysBox(3, Rarity.EPIC, KeyType.GAME_KEY));
            manager.addItemBox(KeyType.GAME_KEY, new KeysBox(4, Rarity.EPIC, KeyType.GAME_KEY));
            manager.addItemBox(KeyType.GAME_KEY, new KeysBox(5, Rarity.LEGENDARY, KeyType.GAME_KEY));
        }

        if (restartTime != null) {
            new RestartServer(restartTime);
        }
    }

    public Location getSpawn() {
        if (spawn == null) {
            spawn = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        }

        Location location = spawn.clone();
        location.setZ(spawn.getZ() + ((Math.random() * 3) - 1.5));
        location.setX(spawn.getX() + ((Math.random() * 3) - 1.5));
        return location;
    }
}
