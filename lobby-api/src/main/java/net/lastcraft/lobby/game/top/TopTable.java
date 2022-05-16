package net.lastcraft.lobby.game.top;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TopTable {

    private final String table;
    private final String column;
    private final String columnFormatLocaleKey;
    private final String holoName;
    private final int type;

}
