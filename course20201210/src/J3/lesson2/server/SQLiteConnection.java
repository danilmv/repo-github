package J3.lesson2.server;

import java.sql.*;

public class SQLiteConnection implements AuthorizationCheck {
    private static SQLiteConnection instance;
    private static Connection connection;

    private SQLiteConnection() throws SQLException {
        connection = getConnection();
        createDataIfEmpty();
    }

    private void createDataIfEmpty() {
        PreparedStatement stm;
        try {
            stm = connection.prepareStatement("SELECT * FROM logins LIMIT 1;");
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                rs.close();
                stm.close();
                System.out.println("SQLite: data exists");
                return;
            }
        } catch (SQLException throwable) {
            System.err.println(throwable.getMessage());
        }

        try {
            System.out.println("SQLite: data doesn't exist... creating initial values....");

            stm = connection.prepareStatement("CREATE TABLE IF NOT EXISTS logins" +
                    "(id integer primary key autoincrement," +
                    "login TEXT NOT NULL," +
                    "password TEXT NOT NULL," +
                    "nickname TEXT NOT NULL);");
            stm.executeUpdate();
            stm.close();

            stm = connection.prepareStatement("INSERT INTO logins(login, password, nickname) VALUES(?, ?, ?)");

            for (int i = 0; i < 10; i++) {
                stm.setString(1, "Test" + i);
                stm.setString(2, "test" + i);
                stm.setString(3, "Name" + i);
                stm.executeUpdate();
            }

            stm.close();

            System.out.println("SQLite: initial data was created");
        } catch (SQLException throwable) {
            System.err.println(throwable.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
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
            System.err.println(throwable.getMessage());
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
            System.err.println(throwables.getMessage());
        }
    }
}
