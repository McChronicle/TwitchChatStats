package db;

import java.sql.*;

public class DBHandler {

    private Connection con;

    public DBHandler() throws ClassNotFoundException, SQLException {

        Class.forName("org.postgresql.Driver");

        this.con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/TwitchTracker",
                "trackerapp", "-+1tsP!1337");
        System.out.println("Verbindung zur Datenbank hergestellt.");

        initDBTables();

    }

    private ResultSet onQuery(String stm) throws SQLException {
        Statement s = this.con.createStatement();
        return s.executeQuery(stm);
    }

    private void onUpdate(String query, Object... params) {
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void initDBTables() {
        this.onUpdate("CREATE TABLE IF NOT EXISTS messages (" +
                "id SERIAL PRIMARY KEY," +
                "username TEXT," +
                "message TEXT," +
                "time TEXT" +
                ")"
        );
    }

    public void saveMessage(String user, String message, String time, String channelName) {
        String stm = "INSERT INTO messages " +
                "(username, message, time, channel) VALUES " +
                "(?, ?, ?, ?)";
        this.onUpdate(stm, user, message, time, channelName);
    }

}
