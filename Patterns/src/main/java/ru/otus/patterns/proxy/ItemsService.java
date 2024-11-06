package ru.otus.patterns.proxy;

import java.sql.Connection;
import java.util.List;

public class ItemsService {

    private final ItemsDao itemsDao = new ItemsDao();

    public void saveItems(Connection connection) {
        for (int i = 1; i <= 100; i++) {
            Item item = new Item(i, "Item " + i, i);
            itemsDao.save(item, connection);
        }
    }

    public void increasePrices(Connection connection) {
        List<Item> items = itemsDao.findAll(connection);
        for (Item item : items) {
            item.setPrice(item.getPrice() * 2);
            itemsDao.update(item, connection);
        }
    }

}