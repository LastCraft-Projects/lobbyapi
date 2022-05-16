package net.lastcraft.lobby.game.old.top.sql;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.sql.ConnectionConstants;
import net.lastcraft.base.sql.SQLConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Deprecated
public class PlayerSelectedLoader {

    private static String table;
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    private static SQLConnection connection;

    public static SQLConnection getConnection() {
        return connection;
    }

    public PlayerSelectedLoader(String table) {
        PlayerSelectedLoader.table = table;
        connection = new SQLConnection("s3" + ConnectionConstants.DOMAIN.getValue(),
                "root", ConnectionConstants.PASSWORD.getValue(), "SelectedTop");
        connect();
    }

    private void connect() {
        connection.execute("CREATE TABLE IF NOT EXISTS `" + table + "` (`playerID` INT(11) PRIMARY KEY, `selected` INT)");
    }

    public static void close() {
        try {
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int getSelected(String name) {
        int playerID = GAMER_MANAGER.getGamer(name).getPlayerID();
        int selected = 1;
        try {
            Statement statement = connection.getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM `" + table + "` WHERE `playerID`='" + playerID + "' LIMIT 1;");
            if (rs.next()) {
                selected = rs.getInt("selected");
            }
            rs.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return selected;
    }

    public static void saveSelect(BukkitGamer gamer, int selected) {
        int playerID = gamer.getPlayerID();
        try {
            Statement statement = connection.getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM `" + table + "` WHERE `playerID` = '" + playerID + "' LIMIT 1;");
            if (rs.next()) {
                connection.execute("UPDATE `" + table + "` SET `selected`='" + selected + "' WHERE `playerID`='" + playerID + "';");
            } else {
                connection.execute("INSERT INTO `" + table + "` (`playerID`, `selected`) VALUES ('" + playerID + "', " + selected + ");");
            }
            rs.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
