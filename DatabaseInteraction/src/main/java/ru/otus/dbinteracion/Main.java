package ru.otus.dbinteracion;

import ru.otus.dbinteracion.connection.DataSource;
import ru.otus.dbinteracion.entity.User;
import ru.otus.dbinteracion.migration.DbMigrator;
import ru.otus.dbinteracion.repository.AbstractRepository;

import java.util.List;

public class Main {

    private static final String url =
            "jdbc:h2:file:./DatabaseInteraction/src/main/resources/h2/db;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";

    public static void main(String[] args) {
        DataSource dataSource = new DataSource(url);

        try {
            dataSource.connect();

            DbMigrator dbMigrator = new DbMigrator(dataSource);
            dbMigrator.migrate();

            System.out.println();
            AbstractRepository<User> userRepository = new AbstractRepository<>(dataSource, User.class);

            for (int i = 0; i < 5; i++) {
                User user = new User(null, "login", "password", "nickname" + i);

                userRepository.save(user);
                System.out.println("Saved user: " + user);
            }

            List<User> users = userRepository.findAll();
            System.out.println("\nUsers in database:\n" + users + "\n");

            for (User user : users) {
                user.setNickname("updated");
                userRepository.update(user);

                Integer id = user.getId();

                System.out.println("Updated user: " + userRepository.findById(id).orElseThrow());
            }

            users = userRepository.findAll();
            System.out.println("\nUsers in database:\n" + users + "\n");

            for (User user : users) {
                Integer id = user.getId();

                userRepository.deleteById(id);

                System.out.println("Deleted user: " + user);
            }

            users = userRepository.findAll();
            System.out.println("\nUsers in database:\n" + users + "\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.close();
        }
    }

}