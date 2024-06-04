package me.caseload.tiers.api.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerMenuUtil {

    private Player owner;
    private OfflinePlayer targetPlayer;
    public PlayerMenuUtil(Player player, OfflinePlayer targetPlayer) {
        this.owner = player;
        this.targetPlayer = targetPlayer;
    }

    public Player getOwner() {
        return owner;
    }

    public OfflinePlayer getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(OfflinePlayer offlinePlayer) {
        targetPlayer = offlinePlayer;
    }
}

