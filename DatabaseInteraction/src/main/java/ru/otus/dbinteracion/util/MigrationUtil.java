package ru.otus.dbinteracion.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class MigrationUtil {

    public static List<File> getMigrations() {
        File folder = new File("./DatabaseInteraction/src/main/resources/migration");

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".sql"));

        if (files == null) {
            return List.of();
        }

        return Arrays.stream(files).toList();
    }

    public static String readMigration(File migration) throws IOException {
        return Files.readString(migration.toPath());
    }

}