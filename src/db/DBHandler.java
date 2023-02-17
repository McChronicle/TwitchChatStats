package db;

import java.sql.*;

public class DBHandler {

    private Connection con;

    public DBHandler() throws ClassNotFoundException, SQLException {

        Class.forName("org.sqlite.JDBC");

        this.con = DriverManager.getConnection("jdbc:sqlite:app.db");
        System.out.println("Verbindung zur Datenbank hergestellt.");

        initDBTables();

    }

    private ResultSet onQuery(String stm) throws SQLException {
        Statement s = this.con.createStatement();
        return s.executeQuery(stm);
    }

    private void onUpdate(String stm) throws SQLException {
        Statement s = this.con.createStatement();
        try {
            s.executeUpdate(stm);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void initDBTables() throws SQLException {
        this.onUpdate("CREATE TABLE IF NOT EXISTS messages (" +
                "user TEXT," +
                "message TEXT," +
                "time TEXT" +
                ")"
        );
    }

    public void saveMessage(String user, String message, String time) throws SQLException {
        String stm = String.format("INSERT INTO messages " +
                "(user, message, time) VALUES " +
                "('%s', '%s', '%s')", user, message, time);
        this.onUpdate(stm);
    }

}
