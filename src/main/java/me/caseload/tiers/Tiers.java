package me.caseload.tiers;

import com.google.common.collect.BiMap;
import me.caseload.tiers.api.PlayerData;
import me.caseload.tiers.api.TierOrderParser;
import me.caseload.tiers.api.manager.ConfigManager;
import me.caseload.tiers.api.papi.TiersExpansion;
import me.caseload.tiers.command.TierCommand;
import me.caseload.tiers.listener.MenuListener;
import me.caseload.tiers.listener.PlayerListener;
import me.neznamy.tab.api.TabAPI;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import static me.caseload.tiers.api.util.FileUtil.backupPlayerData;

public class Tiers extends JavaPlugin {

    public static Tiers plugin;
    public static LuckPerms luckPerms;
    public static TabAPI tabAPI;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Map<String, Integer> tierOrder;

    public static String suffix;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        plugin = this;

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        luckPerms = provider.getProvider();

        tabAPI = TabAPI.getInstance();

        tierOrder = TierOrderParser.parseTierOrder(getConfig());

        suffix = getConfig().getString("suffix");

        ConfigManager.init(this);
        ConfigManager.registerConfigs(
                "menu/tier_set_menu.yml",
                "menu/tier_list_menu.yml",
                "menu/tier_info_menu.yml",
                "menu/tier_testers_menu.yml",
                "menu/confirm_menu.yml"
        );
        //ConfigManager.registerConfigsFromFolder("data");

        registerListeners(
                getServer().getPluginManager(),
                new MenuListener(),
                new PlayerListener()
        );

        new TiersExpansion().register();
        new TierCommand();
    }

    @Override
    public void onDisable() {
        ConfigManager.saveAll();
        backupPlayerData();
    }

    public boolean cooldownEnabled() {
        return getConfig().getBoolean("cooldown.enabled", false);
    }

    private void registerListeners(PluginManager pluginManager, Listener... listeners) {
        for (Listener listener : listeners)
            pluginManager.registerEvents(listener, this);
    }
}
