package ru.otus.patterns.proxy;

import java.sql.Connection;
import java.sql.SQLException;

public class ItemsServiceProxy {

    private final ItemsService itemsService = new ItemsService();

    public void saveItems() {
        try (Connection connection = DataSource.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                System.out.println("Saving items");
                itemsService.saveItems(connection);
                connection.commit();
                System.out.println("Transaction committed: Items saved");
            } catch (Exception e) {
                connection.rollback();
                System.out.println("Transaction rolled back.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void increasePrices() {
        try (Connection connection = DataSource.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try {
                System.out.println("Increasing prices");
                itemsService.increasePrices(connection);
                connection.commit();
                System.out.println("Transaction committed: Prices increased");
            } catch (Exception e) {
                connection.rollback();
                System.out.println("Transaction rolled back");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}