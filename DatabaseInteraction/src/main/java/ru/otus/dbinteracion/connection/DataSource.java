package ru.otus.dbinteracion.connection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.otus.dbinteracion.exception.ConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
@RequiredArgsConstructor
public class DataSource {

    private final String url;

    private Connection connection;

    private Statement statement;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url);
        statement = connection.createStatement();
        System.out.println("Connected to database");
    }

    public void close() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new ConnectionException("Couldn't close statement", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new ConnectionException("Couldn't close connection", e);
            }
        }
        System.out.println("Closed connection with database");
    }
}
