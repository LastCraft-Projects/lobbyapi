package net.lastcraft.lobby.config;

import lombok.Getter;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.Spigot;
import net.lastcraft.api.util.ConfigManager;
import net.lastcraft.dartaapi.utils.core.CoreUtil;
import net.lastcraft.lobby.Lobby;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public abstract class LobbyConfig {

    private static final Spigot SPIGOT = LastCraft.getGamerManager().getSpigot();

    ConfigManager configManager;

    @Getter
    protected final Lobby lobby;

    LobbyConfig(Lobby lobby, String fileName) {
        this.lobby = lobby;
        File file = new File(CoreUtil.getConfigDirectory() + "/" + fileName + ".yml");
        if (!file.exists()) {
            SPIGOT.sendMessage("§c[LOBBY-API] Конфиг " + fileName + " не найден, кажется некоторые вещи работать не будут");
            return;
        }
        this.configManager = new ConfigManager(file);
    }

    public abstract void load();

    public abstract void init();

    public FileConfiguration getConfig() {
        return configManager.getConfig();
    }
}
