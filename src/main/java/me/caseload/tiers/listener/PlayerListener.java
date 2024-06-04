package me.caseload.tiers.listener;

import me.caseload.tiers.Tiers;
import me.caseload.tiers.api.PlayerData;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.nametag.NameTagManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static me.caseload.tiers.Tiers.suffix;
import static me.caseload.tiers.Tiers.tabAPI;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerData playerData = new PlayerData(uuid);
        if (!playerData.isTiered())
            return;

        Tiers.plugin.getServer().getScheduler().runTaskLater(Tiers.plugin, () -> {
            TabPlayer tabPlayer = tabAPI.getPlayer(uuid);
            tabAPI.getNameTagManager().setSuffix(tabPlayer, suffix);
            tabAPI.getTabListFormatManager().setSuffix(tabPlayer, suffix);
        }, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerData playerData = new PlayerData(uuid);
        if (!playerData.isTiered())
            return;

        TabPlayer tabPlayer = tabAPI.getPlayer(uuid);
        tabAPI.getNameTagManager().setSuffix(tabPlayer, "");
        tabAPI.getTabListFormatManager().setSuffix(tabPlayer, "");
    }
}
