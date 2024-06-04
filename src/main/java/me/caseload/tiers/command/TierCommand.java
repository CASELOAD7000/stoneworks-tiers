package me.caseload.tiers.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import me.caseload.tiers.api.PlayerData;
import me.caseload.tiers.api.PlayerDataSorter;
import me.caseload.tiers.api.manager.ConfigManager;
import me.caseload.tiers.api.util.PlayerMenuUtil;
import me.caseload.tiers.menu.Menu;
import me.caseload.tiers.menu.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;

import java.util.stream.Collectors;

public class TierCommand {

    public TierCommand() {
        registerCommand();
    }

    private void registerCommand() {
        new CommandAPICommand("tier")
                .executes((sender, args) -> {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6This server is running the &e'Tiers' &6plugin. &3Author: &bcaseload&3, Discord: &b@caseload&3."));
                })
                .withSubcommand(new CommandAPICommand("list")
                        .withPermission("tier.list")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            PlayerMenuUtil playerMenuUtil = new PlayerMenuUtil(player, player);
                            new PaginatedMenu(
                                    playerMenuUtil,
                                    ConfigManager.fromString("menu/tier_list_menu.yml"),
                                    PlayerDataSorter.getPlayerDataList()).open();
                        })
                )
                .withSubcommand(new CommandAPICommand("info")
                        .withPermission("tier.info")
                        .withArguments(new OfflinePlayerArgument("target"))
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            OfflinePlayer targetPlayer = (OfflinePlayer) args.get("target");
                            if (targetPlayer == null || (!targetPlayer.isOnline() && !targetPlayer.hasPlayedBefore())) {
                                sender.sendMessage(ChatColor.RED + "That player has not played before");
                                return;
                            }

                            PlayerData playerData = new PlayerData(targetPlayer.getUniqueId());
                            if (!playerData.isTiered()) {
                                sender.sendMessage(ConfigManager.getConfigMessage(player, "not_tiered_message"));
                                return;
                            }

                            PlayerMenuUtil playerMenuUtil = new PlayerMenuUtil(player, targetPlayer);
                            new Menu(
                                    playerMenuUtil,
                                    ConfigManager.fromString("menu/tier_info_menu.yml")
                            ).open();
                        })
                )
                .withSubcommand(new CommandAPICommand("set")
                        .withPermission("tier.set")
                        .withArguments(new OfflinePlayerArgument("target"))
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            OfflinePlayer targetPlayer = (OfflinePlayer) args.get("target");
                            if (targetPlayer == null || (!targetPlayer.isOnline() && !targetPlayer.hasPlayedBefore())) {
                                sender.sendMessage(ChatColor.RED + "That player has not played before");
                                return;
                            }

                            PlayerMenuUtil playerMenuUtil = new PlayerMenuUtil(player, targetPlayer);
                            new Menu(
                                    playerMenuUtil,
                                    ConfigManager.fromString("menu/tier_set_menu.yml")
                            ).open();
                        })
                )
                .withSubcommand(new CommandAPICommand("remove")
                        .withPermission("tier.remove")
                        .withArguments(new OfflinePlayerArgument("target"))
                        .executes((sender, args) -> {
                            OfflinePlayer targetPlayer = (OfflinePlayer) args.get("target");
                            if (targetPlayer == null || (!targetPlayer.isOnline() && !targetPlayer.hasPlayedBefore())) {
                                sender.sendMessage(ChatColor.RED + "That player has not played before");
                                return;
                            }

                            PlayerData playerData = new PlayerData(targetPlayer.getUniqueId());
                            if (!playerData.isTiered()) {
                                sender.sendMessage(ConfigManager.getConfigMessage(targetPlayer, "not_tiered_message"));
                                return;
                            }

                            playerData.clearData();
                            sender.sendMessage(ConfigManager.getConfigMessage(targetPlayer, "tier_removed_message"));
                        })
                )
                .withSubcommand(new CommandAPICommand("testers")
                        .withPermission("tier.testers")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            PlayerMenuUtil playerMenuUtil = new PlayerMenuUtil(player, player);
                            new PaginatedMenu(
                                    playerMenuUtil,
                                    ConfigManager.fromString("menu/tier_testers_menu.yml"),
                                    PlayerDataSorter.getPlayerDataList().stream()
                                            .filter(PlayerData::isTester)
                                            .collect(Collectors.toList())
                            ).open();
                        })
                        .withSubcommand(new CommandAPICommand("add")
                                .withPermission("tier.testers.add")
                                .withArguments(new OfflinePlayerArgument("player"))
                                .executes((sender, args) -> {
                                    OfflinePlayer player = (OfflinePlayer) args.get("player");
                                    if (player == null || (!player.isOnline() && !player.hasPlayedBefore())) {
                                        sender.sendMessage(ChatColor.RED + "That player has not played before");
                                        return;
                                    }

                                    PlayerData playerData = new PlayerData(player.getUniqueId());
                                    if (!playerData.isTiered()) {
                                        sender.sendMessage(ConfigManager.getConfigMessage(player, "tester_not_tiered_message"));
                                        return;
                                    }

                                    if (playerData.isTester()) {
                                        sender.sendMessage(ConfigManager.getConfigMessage(player, "already_tester_message"));
                                        return;
                                    }

                                    playerData.setTester(true);
                                    sender.sendMessage(ConfigManager.getConfigMessage(player, "tester_add_message"));
                                })
                        )
                        .withSubcommand(new CommandAPICommand("remove")
                                .withPermission("tier.testers.remove")
                                .withArguments(new OfflinePlayerArgument("player"))
                                .executes((sender, args) -> {
                                    OfflinePlayer player = (OfflinePlayer) args.get("player");
                                    if (player == null || (!player.isOnline() && !player.hasPlayedBefore())) {
                                        sender.sendMessage(ChatColor.RED + "That player has not played before");
                                        return;
                                    }

                                    PlayerData playerData = new PlayerData(player.getUniqueId());
                                    if (!playerData.isTester()) {
                                        sender.sendMessage(ConfigManager.getConfigMessage(player, "not_tester_message"));
                                        return;
                                    }

                                    playerData.setTester(false);
                                    sender.sendMessage(ConfigManager.getConfigMessage(player, "tester_remove_message"));
                                })
                        )
                ).register();
    }
}