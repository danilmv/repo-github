package J2.lesson8;

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
    public boolean checkAuthorization(String login, String password) {
        if (connection == null) return false;

        try {
            PreparedStatement stm = connection.prepareStatement("select login from chatAuth where login = '" + login
                    + "' and pass = '" + password + "' limit 1;");
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            System.err.println(throwables.getMessage());
        }

        return false;
    }

    public boolean isConnected() {
        return connection != null;
    }
}
