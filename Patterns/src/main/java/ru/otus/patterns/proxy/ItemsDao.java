package ru.otus.patterns.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemsDao {

    public void save(Item item, Connection connection) {
        String sql = "INSERT INTO items (id, title, price) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, item.getId());
            preparedStatement.setString(2, item.getTitle());
            preparedStatement.setInt(3, item.getPrice());
            preparedStatement.executeUpdate();
            System.out.println("Saved item:" + item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Item item, Connection connection) {
        String sql = "UPDATE items SET title = ?, price = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, item.getTitle());
            preparedStatement.setInt(2, item.getPrice());
            preparedStatement.setInt(3, item.getId());
            preparedStatement.executeUpdate();
            System.out.println("Updated item:" + item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Item item, Connection connection) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, item.getId());
            pstmt.executeUpdate();
            System.out.println("Deleted item:" + item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Item> findAll(Connection connection) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new Item(rs.getInt("id"), rs.getString("title"), rs.getInt("price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

}