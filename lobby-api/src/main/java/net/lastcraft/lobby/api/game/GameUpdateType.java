package net.lastcraft.lobby.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
@Getter
public enum GameUpdateType {
    DEFAULT(null, ChatColor.AQUA),
    UPDATE("GAMEMENU_ITEM_UPDATE_NAME", ChatColor.YELLOW),
    NEW("GAMEMENU_ITEM_NEW_NAME", ChatColor.RED),
    WIPE("GAMEMENU_ITEM_WIPE", ChatColor.YELLOW);

    private final String key;
    private final ChatColor chatColor;
}
