package net.lastcraft.lobby.game.top;

import net.lastcraft.base.sql.ConnectionConstants;
import net.lastcraft.base.sql.api.MySqlDatabase;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class TopSql {

    private static final String SQL_TOP_QUERY = "SELECT * FROM `%s` WHERE `type` = ? ORDER BY `%s` DESC LIMIT %s;";

    private final MySqlDatabase database;
    private final int limit;

    TopSql(int limit) {
        this.database = MySqlDatabase.newBuilder()
                .data("GameStats")
                .host("s3" + ConnectionConstants.DOMAIN.getValue())
                .password(ConnectionConstants.PASSWORD.getValue())
                .user("root")
                .create();

        this.limit = limit;
    }

    void update(Map<TopTable, List<TopStandData>> topTableListMap) {
        for (Map.Entry<TopTable, List<TopStandData>> data : topTableListMap.entrySet()) {
            String nameTable = data.getKey().getTable();
            String nameColumn = data.getKey().getColumn();
            int type = data.getKey().getType();

            List<TopStandData> topStandData = data.getValue();

            AtomicInteger size = new AtomicInteger();
            database.executeQuery(String.format(SQL_TOP_QUERY, nameTable, nameColumn, limit), (rs) -> {
                while (rs.next()) {
                    int playerID = rs.getInt("userID");
                    int value = rs.getInt(nameColumn);
                    topStandData.get(size.get()).update(playerID, value);
                    size.getAndIncrement();
                }

                return Void.TYPE;
            }, type);
        }
    }

    void onClose() {
        database.close();
    }
}
