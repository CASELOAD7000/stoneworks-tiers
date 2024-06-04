package me.caseload.tiers.api.util;

import me.caseload.tiers.Tiers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtil {

    public static void backupPlayerData() {
        File backupsDir = new File(Tiers.plugin.getDataFolder(), "backups");
        if (!backupsDir.exists())
            backupsDir.mkdir();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        File backupDir = new File(backupsDir, timestamp);
        backupDir.mkdir();

        File dataDir = new File(Tiers.plugin.getDataFolder(), "data");
        try {
            Files.walk(dataDir.toPath()).forEach(source -> copyFile(source, dataDir.toPath(), backupDir.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File[] backups = backupsDir.listFiles();
        if (backups != null && backups.length > 3) {
            Arrays.sort(backups, Comparator.comparingLong(File::lastModified));
            for (int i = 0; i < backups.length - 3; i++) {
                deleteDirectory(backups[i]);
            }
        }
    }

    public static void copyFile(java.nio.file.Path source, java.nio.file.Path dataDir, java.nio.file.Path backupDir) {
        File dest = new File(backupDir.toFile(), dataDir.relativize(source).toString());
        try {
            if (source.toFile().isDirectory()) {
                dest.mkdir();
            } else {
                Files.copy(source, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }
}
