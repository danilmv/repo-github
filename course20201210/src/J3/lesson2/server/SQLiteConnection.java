package J3.lesson2.server;

import java.sql.*;

public class SQLiteConnection implements AuthorizationCheck {
    private static SQLiteConnection instance;
    private static Connection connection;

    private SQLiteConnection() throws SQLException {
        connection = getConnection();
        if (!isDataExists()) {
            Server.getLOGGER().config("SQLite: data doesn't exist...");
            createInitialData();
        }
    }

    private boolean isDataExists() {
        try (
                PreparedStatement stm = connection.prepareStatement("SELECT * FROM logins LIMIT 1;");
                ResultSet rs = stm.executeQuery();
        ) {
            if (rs.next()) {
                Server.getLOGGER().config("SQLite: data exists");
                return true;
            } else
                return false;
        } catch (SQLException throwable) {
            System.err.println(throwable.getMessage());
            return false;
        }
    }

    private void createInitialData() {
        try {
            Server.getLOGGER().config("SQLite: creating initial data....");

            Statement stm = connection.createStatement();
            stm.executeUpdate("CREATE TABLE IF NOT EXISTS logins" +
                    "(id integer primary key autoincrement," +
                    "login TEXT NOT NULL," +
                    "password TEXT NOT NULL," +
                    "nickname TEXT NOT NULL);");
            stm.close();

            PreparedStatement pStm = connection.prepareStatement("INSERT INTO logins(login, password, nickname) VALUES(?, ?, ?)");

            for (int i = 0; i < 10; i++) {
                pStm.setString(1, "Test" + i);
                pStm.setString(2, "test" + i);
                pStm.setString(3, "Name" + i);
                pStm.executeUpdate();
            }

            pStm.close();

            Server.getLOGGER().config("SQLite: initial data was created");
        } catch (SQLException throwable) {
            Server.getLOGGER().severe(throwable.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:sqlite:SQLite.db");
        }

        return connection;
    }

    public static SQLiteConnection getInstance() throws SQLException {
        if (instance == null) instance = new SQLiteConnection();
        return instance;
    }

    @Override
    public String checkAuthorization(String login, String password) {
        if (connection == null)
            return null;

        try {
            PreparedStatement stm = connection.prepareStatement("SELECT nickname FROM logins WHERE login = '"
                    + login
                    + "' AND password = '" + password + "' LIMIT 1;");
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
            rs.close();
            stm.close();
        } catch (SQLException throwable) {
            Server.getLOGGER().severe(throwable.getMessage());
        }

        return null;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void changeNickname(String login, String nickname) {
        if (connection == null)
            return;

        try {
            PreparedStatement stm = connection.prepareStatement("UPDATE logins SET nickname = '" + nickname
                    + "' WHERE login = '" + login + "';");
            stm.executeUpdate();
            stm.close();
        } catch (SQLException throwables) {
            Server.getLOGGER().severe(throwables.getMessage());
        }
    }
}
