package ru.otus.dbinteracion.migration;

import lombok.RequiredArgsConstructor;
import ru.otus.dbinteracion.connection.DataSource;
import ru.otus.dbinteracion.exception.MigrationException;
import ru.otus.dbinteracion.util.MigrationUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class DbMigrator {

    private final DataSource dataSource;

    public void migrate() {
        try {
            Connection connection = dataSource.getConnection();
            createMigrationHistoryTable(connection);

            List<File> migrations = MigrationUtil.getMigrations();

            for (File migration : migrations) {
                String migrationName = migration.getName();
                if (!isMigrationExecuted(connection, migrationName)) {
                    executeMigration(connection, migration);
                    System.out.printf("Migration %s executed%n", migrationName);
                } else {
                    System.out.printf("Migration %s skipped%n", migrationName);
                }
            }
        } catch (SQLException e) {
            throw new MigrationException("Database migration failed", e);
        }
    }

    private void createMigrationHistoryTable(Connection connection) throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS migration_history (" +
                "id SERIAL PRIMARY KEY, " +
                "script_name VARCHAR(255) UNIQUE, " +
                "applied_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try (PreparedStatement ps = connection.prepareStatement(createTableQuery)) {
            ps.executeUpdate();
        }
    }

    private boolean isMigrationExecuted(Connection connection, String migrationName) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM migration_history WHERE script_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkQuery)) {
            ps.setString(1, migrationName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    private void executeMigration(Connection connection, File migration) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(MigrationUtil.readMigration(migration))) {
            ps.executeUpdate();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String insertHistoryQuery = "INSERT INTO migration_history (script_name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(insertHistoryQuery)) {
            ps.setString(1, migration.getName());
            ps.executeUpdate();
        }
    }

}