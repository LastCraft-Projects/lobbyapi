package net.lastcraft.lobby.game.old.top.sql;

import net.lastcraft.base.sql.ConnectionConstants;
import net.lastcraft.base.sql.api.MySqlDatabase;
import net.lastcraft.lobby.game.old.top.TopConfig;
import net.lastcraft.lobby.game.old.top.data.TopData;
import net.lastcraft.lobby.game.old.top.data.TopPlayer;

import java.sql.ResultSet;
import java.sql.Statement;

@Deprecated
public class TopLoader {
    private final MySqlDatabase connection;

    public TopLoader() {
        connection = MySqlDatabase.newBuilder()
                .data("Stats")
                .host("s3" + ConnectionConstants.DOMAIN.getValue())
                .password(ConnectionConstants.PASSWORD.getValue())
                .user("root")
                .create();
    }

    public void close() {
        try {
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateTops() {
        try {
            int amount = TopConfig.getLocations().size();
            Statement statement = connection.getConnection().createStatement();
            for (TopData topData : TopData.getTopDatas().values()) {
                topData.getPlayersTop().clear();
                String sql = "SELECT * FROM `" + topData.getTable() + "` ORDER BY `Wins` DESC LIMIT " + amount;
                ResultSet resultSet = statement.executeQuery(sql);
                int place = 1;
                while (resultSet.next()) {
                    int playerID = resultSet.getInt("PlayerID");
                    int statistic = resultSet.getInt("Wins");
                    TopPlayer topPlayer = new TopPlayer(statistic, playerID);
                    topData.getPlayersTop().put(place, topPlayer);
                    place++;
                }
                resultSet.close();
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
