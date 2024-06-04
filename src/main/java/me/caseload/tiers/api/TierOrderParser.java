package me.caseload.tiers.api;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class TierOrderParser {

    public static Map<String, Integer> parseTierOrder(FileConfiguration config) {
        Map<String, Integer> tierOrder = new HashMap<>();

        for (String key : config.getConfigurationSection("tiers").getKeys(false)) {
            int order = Integer.parseInt(key);
            String tierName = config.getString("tiers." + key);
            tierOrder.put(tierName.toUpperCase(), order);
        }

        return tierOrder;
    }
}
