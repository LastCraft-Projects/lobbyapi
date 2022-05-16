package net.lastcraft.rewards.managers;

import net.lastcraft.base.sql.ConnectionConstants;
import net.lastcraft.base.sql.api.MySqlDatabase;
import net.lastcraft.base.sql.api.table.ColumnType;
import net.lastcraft.base.sql.api.table.TableColumn;
import net.lastcraft.base.sql.api.table.TableConstructor;
import net.lastcraft.rewards.bonuses.RewardType;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public final class RewardManager {

    private static final MySqlDatabase MY_SQL_DATABASE = MySqlDatabase.newBuilder()
            .host("s3" + ConnectionConstants.DOMAIN.getValue())
            .password(ConnectionConstants.PASSWORD.getValue())
            .user("root")
            .data("lobby").create();

    private static final String GET_USER_REWARDS = "SELECT * FROM `rewards` WHERE `userID` = ?";
    private static final String GET_USER_REWARDS_BY_TYPE = "SELECT * FROM `rewards` WHERE `userID` = ? AND `type` = ? LIMIT 1";
    private static final String UPDATE_USER_REWARDS = "UPDATE `rewards` set `time` = ? WHERE `userID` = ? AND `type` = ? LIMIT 1";
    private static final String ADD_USER_REWARDS = "INSERT INTO `rewards` (`userID`, `type`, `time`) VALUES (?, ?, ?)";

    public RewardManager() {
        ensureTables();
    }

    private void ensureTables() {
        new TableConstructor("rewards",
                new TableColumn("id", ColumnType.INT_11).autoIncrement(true).primaryKey(true),
                new TableColumn("userID", ColumnType.INT_11),
                new TableColumn("type", ColumnType.INT),
                new TableColumn("time", ColumnType.TIMESTAMP)
        ).create(MY_SQL_DATABASE);
    }

    /**
     * Загрузить список наград игрока
     *
     * @param id - id игрока
     * @return Map - список наград
     */
    public Map<RewardType, Long> loadRewards(int id) {
        return MY_SQL_DATABASE.executeQuery(GET_USER_REWARDS, rs -> {
            Map<RewardType, Long> rewards = new HashMap<>();

            while (rs.next()) {
                RewardType type = RewardType.getRewardType(rs.getInt("type"));
                if (type != null) {
                    long time = rs.getTimestamp("time").getTime();

                    //if (System.currentTimeMillis() + type.getTimeDelay() < time)
                    //    MY_SQL_DATABASE.execute(MysqlQuery.deleteFrom("rewards")
                    //            .where("userID", QuerySymbol.EQUALLY, id)
                    //            .where("type", QuerySymbol.EQUALLY, type.getId()));

                    rewards.put(type, time);
                }
            }

            return rewards;
        }, id);
    }

    /**
     * Сохранить данные об игроке
     *
     * @param id - id игрока
     * @param type - тип награды
     */
    public void saveData(int id, RewardType type) {
        if (id == -1)
            return;

        MY_SQL_DATABASE.executeQuery(GET_USER_REWARDS_BY_TYPE, rs -> {
            if (rs.next()) {
                MY_SQL_DATABASE.execute(UPDATE_USER_REWARDS, new Timestamp(System.currentTimeMillis()), id, type.getId());
            } else {
                MY_SQL_DATABASE.execute(ADD_USER_REWARDS, id, type.getId(), new Timestamp(System.currentTimeMillis()));
            }

            return Void.TYPE;
        }, id, type.getId());
    }
}
