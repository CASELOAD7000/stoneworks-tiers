package me.caseload.tiers.api.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.caseload.tiers.Tiers;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {

    private static Plugin plugin;
    private static final BiMap<String, FileConfiguration> configs = HashBiMap.create();
    private static FileConfiguration langConfig;

    public static void init(Plugin plugin) {
        ConfigManager.plugin = plugin;
        langConfig = loadConfig("messages.yml");
    }

    public static void registerConfigs(String... paths) {
        for (String path : paths) {
            configs.put(path, loadConfig(path));
        }
    }

    public static void registerConfig(String path) {
        configs.put(path, loadConfig(path));
    }

    public static void registerConfigsFromFolder(String folderPath) {
        File folder = new File(plugin.getDataFolder(), folderPath);
        if (!folder.exists() || !folder.isDirectory())
            return;

        File[] files = folder.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                registerConfig(folderPath + "/" + file.getName());
                Tiers.plugin.getLogger().info(folderPath + "/" + file.getName() + " was registered");
            }
        }
    }

    public static void reloadConfig(String path) {
        if (!configs.containsKey(path))
            return;

        File configFile = new File(plugin.getDataFolder(), path);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        configs.put(path, config);
    }

    public static void reloadAllConfigs() {
        Set<String> keys = new HashSet<>(configs.keySet());

        for (String path : keys)
            reloadConfig(path);
    }

    public static FileConfiguration fromString(String path) {
        return configs.get(path);
    }

    public static void save(FileConfiguration config) {
        save(configs.inverse().get(config));
    }

    public static void save(String path) {
        if (!configs.containsKey(path))
            return;

        File configFile = new File(plugin.getDataFolder(), path);
        FileConfiguration config = configs.get(path);
        try {
            config.save(configFile);
        } catch (IOException exception) {
            plugin.getLogger().severe("Couldn't save " + config.getCurrentPath() + ": " + exception);
        }
    }

    public static void saveAll() {
        for (String path : configs.keySet())
            save(path);
    }

    public static void sendConfigMessage(Player player, String path) {
        player.sendMessage(getConfigMessage(player, path));
    }

    public static String getConfigFormat(OfflinePlayer player, String path) {
        String message = ChatColor.translateAlternateColorCodes(
                '&',
                plugin.getConfig().getString(path, "&cThat path does not exist!")
        );

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            message = PlaceholderAPI.setPlaceholders(player, message);

        return message;
    }

    public static String getConfigFormat(FileConfiguration config, OfflinePlayer player, String path) {
        String message = ChatColor.translateAlternateColorCodes(
                '&',
                config.getString(path, "&cThat path does not exist!")
        );

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            message = PlaceholderAPI.setPlaceholders(player, message);

        return message;
    }

    public static String getConfigMessage(OfflinePlayer player, String path) {
        String message = ChatColor.translateAlternateColorCodes(
                '&',
                langConfig.getString("prefix", "&8[&eTiers8] ") + langConfig.getString(path, "&cThat message does not exist!")
        );

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            message = PlaceholderAPI.setPlaceholders(player, message);

        return message;
    }

    private static FileConfiguration loadConfig(String path) {
        File configFile = new File(plugin.getDataFolder(), path);
        if (!configFile.exists())
            plugin.saveResource(path, false);

        return YamlConfiguration.loadConfiguration(configFile);
    }
}
