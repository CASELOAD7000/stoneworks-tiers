package me.caseload.tiers.api;

import me.caseload.tiers.Tiers;
import me.caseload.tiers.api.manager.ConfigManager;
import me.neznamy.tab.api.TabPlayer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static me.caseload.tiers.Tiers.suffix;
import static me.caseload.tiers.Tiers.tabAPI;

public class PlayerData {

    private final UUID uuid;
    private FileConfiguration dataConfig;
    private File dataFile;
    private boolean isDataLoaded = false;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        //this.dataConfig = ConfigManager.fromString("data/" + uuid + ".yml");
        this.dataFile = new File(Tiers.plugin.getDataFolder(), "data/" + uuid + ".yml");
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public boolean dataExists() {
        return dataFile.exists();
    }

    public String getTier() {
        loadData();
        return dataConfig.getString("tier", null);
    }

    public boolean isTiered() {
        if (!dataExists())
            return false;
        return getTier() != null;
    }

    public String getLatestTest() {
        loadData();
        return dataConfig.getString("latest_test", null);
    }

    public String getLatestTestOther() {
        loadData();
        return dataConfig.getString("tester_stats.latest_test", null);
    }

    public int getTests() {
        loadData();
        return dataConfig.getInt("tester_stats.total_tests", 0);
    }

    public void setTests(int num) {
        loadData();
        dataConfig.set("tester_stats.total_tests", num);
        saveConfig();
    }

    public void setTier(String string) {
        loadData();

        dataConfig.set("tier", string);
        saveConfig();

        if (getOfflinePlayer().isOnline()) {
            ConfigManager.sendConfigMessage(getOfflinePlayer().getPlayer(), "tier_achieved_message");

            TabPlayer tabPlayer = tabAPI.getPlayer(uuid);
            tabAPI.getNameTagManager().setSuffix(tabPlayer, suffix);
            tabAPI.getTabListFormatManager().setSuffix(tabPlayer, suffix);
        }
    }

    public void setTester(boolean bool) {
        loadData();
        dataConfig.set("tester", bool);

        setPermission(Tiers.luckPerms, "tier.set", bool);
        setPermission(Tiers.luckPerms, "tier.testers", bool);

        if (bool)
            dataConfig.set("tester_stats.latest_test", LocalDateTime.now().format(Tiers.formatter));

        saveConfig();

        if (getOfflinePlayer().isOnline())
            ConfigManager.sendConfigMessage(getOfflinePlayer().getPlayer(), "tester_achieved_message");
    }

    public void setLatestTest(String string) {
        loadData();
        dataConfig.set("latest_test", string);
        saveConfig();
    }

    public void setLatestTestOther(String string) {
        loadData();
        dataConfig.set("tester_stats.latest_test", string);
        saveConfig();
    }

    public String getCooldownDuration() {
        if (!Tiers.plugin.cooldownEnabled() || !hasCooldown())
            return null;

        LocalDateTime latestTestDate = LocalDateTime.parse(getLatestTest(), Tiers.formatter);
        LocalDateTime endDate = latestTestDate.plusDays(Tiers.plugin.getConfig().getInt("cooldown.duration"));
        Duration duration = Duration.between(LocalDateTime.now(), endDate);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        if (days > 0)
            return String.format("%dd, %dh", days, hours);
        else if (hours > 0)
            return String.format("%dh, %dm", hours, minutes);
        else if (minutes > 0)
            return String.format("%dm, %ds", minutes, seconds);
        else
            return String.format("%ds", seconds);
    }

    public boolean hasCooldown() {
        if (!Tiers.plugin.cooldownEnabled())
            return false;

        String latestTest = getLatestTest();
        if (latestTest == null)
            return false;

        LocalDateTime latestTestDate = LocalDateTime.parse(latestTest, Tiers.formatter);
        LocalDateTime endDate = latestTestDate.plusDays(Tiers.plugin.getConfig().getInt("cooldown.duration"));
        LocalDateTime currentDate = LocalDateTime.now();

        return currentDate.isBefore(endDate);
    }

    public boolean isActive() {
        loadData();

        String latestTest = getLatestTestOther();
        if (latestTest == null)
            return false;

        LocalDateTime testDate = LocalDateTime.parse(latestTest, Tiers.formatter);
        LocalDateTime endDate = testDate.plusDays(Tiers.plugin.getConfig().getInt("tester_inactive_threshold"));

        return LocalDateTime.now().isBefore(endDate);
    }

    public boolean isTester() {
        loadData();
        return dataConfig.getBoolean("tester", false);
    }

    public void saveConfig() {
        try {
            if (!dataFile.getParentFile().exists())
                dataFile.getParentFile().mkdirs();
            dataConfig.save(dataFile);
        } catch (IOException exception) {
            Tiers.plugin.getLogger().severe("Couldn't save " + dataFile.getPath() + ": " + exception);
        }
    }

    private void loadData() {
        if (!isDataLoaded) {
            if (dataFile.exists()) {
                this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
            } else {
                this.dataConfig = new YamlConfiguration();
            }
            isDataLoaded = true;
        }
    }

    public void clearData() {
        loadData();
        dataConfig.set("tier", null);
        dataConfig.set("latest_test", null);
        dataConfig.set("tester", null);
        dataConfig.set("tester_stats", null);
        saveConfig();

        if (getOfflinePlayer().isOnline()) {
            TabPlayer tabPlayer = tabAPI.getPlayer(uuid);
            tabAPI.getNameTagManager().setSuffix(tabPlayer, "");
            tabAPI.getTabListFormatManager().setSuffix(tabPlayer, "");
        }

        setPermission(Tiers.luckPerms, "tier.set", false);
        setPermission(Tiers.luckPerms, "tier.testers", false);
    }

    private void setPermission(LuckPerms luckPerms, String permission, boolean value) {
        User user = luckPerms.getUserManager().getUser(uuid);

        if (user != null) {
            Node node = PermissionNode.builder(permission)
                    .value(value)
                    .build();

            if (value)
                user.data().add(node);
            else
                user.data().remove(node);

            luckPerms.getUserManager().saveUser(user);
        }
    }
}