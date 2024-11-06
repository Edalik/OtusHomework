package ru.otus.patterns.proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private static final DataSource INSTANCE = new DataSource();

    private final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    private final String user = "user";

    private final String password = "password";

    private DataSource() {
        try (Connection conn = getConnection()) {
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS items (id INT PRIMARY KEY, title VARCHAR(255), price INT)");
        } catch (SQLException e) {
            System.out.println("Error creating table");
            e.printStackTrace();
        }
    }

    public static DataSource getInstance() {
        return DataSource.INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}