package ru.otus.patterns.proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private static volatile DataSource instance;

    private final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    private final String user = "user";

    private final String password = "password";

    private DataSource() {
        try (Connection conn = getConnection()) {
            conn.createStatement().execute("CREATE TABLE items (id INT PRIMARY KEY, title VARCHAR(255), price INT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DataSource getInstance() {
        DataSource localInstance = instance;

        if (localInstance == null) {
            synchronized (DataSource.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = new DataSource();
                }
            }
        }

        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}