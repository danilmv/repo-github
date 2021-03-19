package J3.lesson2.server;

import java.sql.*;

public class MySQLConnection implements AuthorizationCheck {
    private static final String name = "root";
    private static final String ulr = "jdbc:mysql://127.0.0.1:3306/example";
    private static final String pass = "enter your password here";
    private final Connection connection;


    public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(ulr, name, pass);
    }

    public MySQLConnection() throws SQLException {
        connection = getConnection();
    }

    @Override
    public String checkAuthorization(String login, String password) {
        if (connection == null) return null;

        try {
            PreparedStatement stm = connection.prepareStatement("SELECT login FROM chatAuth WHERE login = '" + login
                    + "' AND pass = '" + password + "' LIMIT 1;");
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException throwables) {
            System.err.println(throwables.getMessage());
        }

        return null;
    }

    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
