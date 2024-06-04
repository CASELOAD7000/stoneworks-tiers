package me.caseload.tiers.api.papi;

import me.caseload.tiers.api.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class TiersExpansion extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "tiers";
    }

    @Override
    public String getAuthor() {
        return "Caseload";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null)
            return "";

        PlayerData playerData = new PlayerData(player.getUniqueId());

        if (params.equals("tier"))
            return playerData.getTier();
        else if (params.equals("cooldown"))
            return playerData.getCooldownDuration();
        else if (params.equals("tests"))
            return String.valueOf(playerData.getTests());
        else if (params.equals("isactive"))
            return String.valueOf(playerData.isActive());

        return null;
    }
}
