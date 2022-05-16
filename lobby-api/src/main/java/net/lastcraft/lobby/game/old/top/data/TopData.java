package net.lastcraft.lobby.game.old.top.data;

import lombok.Getter;
import net.lastcraft.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

@Deprecated
@Getter
public class TopData {

    private static final Map<Integer, TopData> TOP_DATA = new HashMap<>();

    public static Map<Integer, TopData> getTopDatas(){
        return TOP_DATA;
    }

    public static int getNext(int i) {
        int selected = i + 1;
        return (selected > TOP_DATA.size() ? 1 : selected);
    }

    private final String table;
    private final String type;
    private final Map<Integer, TopPlayer> playersTop = new HashMap<>();

    public TopData(int position, String table, String type) {
        this.table = table;
        this.type = type;
        TOP_DATA.put(position, this);
    }

    public String getSecondLineHolo(int stats, int lang) {
        return StringUtil.getCorrectWord(stats, "WINS_1", lang);
    }
}
