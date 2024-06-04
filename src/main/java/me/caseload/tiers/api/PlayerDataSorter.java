package me.caseload.tiers.api;

import me.caseload.tiers.Tiers;

import java.io.File;
import java.util.*;

public class PlayerDataSorter {

    private final Map<String, Integer> tierOrder;

    public PlayerDataSorter(Map<String, Integer> tierOrder) {
        this.tierOrder = tierOrder;
    }

    public void sortByTier(List<PlayerData> playerDataList) {
        playerDataList.sort(Comparator.comparingInt(playerData -> {
            String tier = playerData.getTier();
            return tier != null ? tierOrder.getOrDefault(tier.toUpperCase(), Integer.MAX_VALUE) : Integer.MAX_VALUE;
        }));
    }

    public static List<PlayerData> getPlayerDataList() {
        List<PlayerData> playerDataList = new ArrayList<>();
        File dataFolder = new File(Tiers.plugin.getDataFolder(), "data");

        if (!dataFolder.exists() || !dataFolder.isDirectory())
            return playerDataList;

        File[] dataFiles = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (dataFiles == null)
            return playerDataList;

        for (File file : dataFiles) {
            String fileName = file.getName();
            UUID uuid = UUID.fromString(fileName.substring(0, fileName.length() - 4));
            PlayerData playerData = new PlayerData(uuid);
            playerDataList.add(playerData);
        }

        new PlayerDataSorter(Tiers.tierOrder).sortByTier(playerDataList);
        return playerDataList;
    }
}
