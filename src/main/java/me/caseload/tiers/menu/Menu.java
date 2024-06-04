package me.caseload.tiers.menu;

import me.caseload.tiers.Tiers;
import me.caseload.tiers.api.PlayerData;
import me.caseload.tiers.api.manager.ConfigManager;
import me.caseload.tiers.api.util.ItemBuilder;
import me.caseload.tiers.api.util.PlayerMenuUtil;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.UUID;

import static me.caseload.tiers.Tiers.tierOrder;

public class Menu extends me.caseload.tiers.api.menu.Menu {

    private final FileConfiguration menuConfig;
    private final UUID targetId;
    private String tier;

    private PlayerData playerData;

    public Menu(PlayerMenuUtil playerMenuUtil, FileConfiguration menuConfig) {
        super(playerMenuUtil);
        this.menuConfig = menuConfig;
        this.targetId = playerMenuUtil.getTargetPlayer().getUniqueId();
        this.playerData = new PlayerData(targetId);
    }

    public Menu(PlayerMenuUtil playerMenuUtil, FileConfiguration menuConfig, String tier) {
        super(playerMenuUtil);
        this.menuConfig = menuConfig;
        this.targetId = playerMenuUtil.getTargetPlayer().getUniqueId();
        this.playerData = new PlayerData(targetId);
        this.tier = tier;
    }

    @Override
    public String getMenuName() {
        return menuConfig.getString("name");
    }

    @Override
    public int getSlots() {
        return menuConfig.getInt("size");
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        ConfigurationSection itemSection = menuConfig.getConfigurationSection("items");
        if (itemSection == null)
            return;

        ConfigurationSection slotSection = itemSection.getConfigurationSection(String.valueOf(event.getSlot()));
        if (slotSection == null)
            return;

        String action = slotSection.getString("action");
        if (action == null)
            return;

        Player player = (Player) event.getWhoClicked();

        switch (action) {
            case "SET_TIER" -> {
                String tier = slotSection.getString("tier", null);
                PlayerData testerData = new PlayerData(playerMenuUtil.getOwner().getUniqueId());

                if (testerData.isTester()
                        && tierOrder.get(tier) < tierOrder.get(testerData.getTier())
                        && !playerMenuUtil.getOwner().hasPermission("tier.weight_bypass")) {
                    playerMenuUtil.getOwner().sendMessage(ConfigManager.getConfigMessage(playerMenuUtil.getTargetPlayer(), "tier_weight_message"));
                    return;
                }

                if (playerData.hasCooldown()) {
                    playerMenuUtil.getOwner().sendMessage(ConfigManager.getConfigMessage(playerMenuUtil.getTargetPlayer(), "cooldown_warning_message"));
                    new Menu(playerMenuUtil, ConfigManager.fromString("menu/confirm_menu.yml"), tier).open();
                    return;
                }

                playerData.setTier(tier);
                playerData.setLatestTest(LocalDateTime.now().format(Tiers.formatter));

                if (testerData.dataExists() && testerData.isTester()) {
                    testerData.setTests(testerData.getTests() + 1);
                    testerData.setLatestTestOther(LocalDateTime.now().format(Tiers.formatter));
                }

                playerMenuUtil.getOwner().sendMessage(ConfigManager.getConfigMessage(playerMenuUtil.getTargetPlayer(), "tier_set_message"));
                playerMenuUtil.getOwner().closeInventory();
            }
            case "CONFIRM" -> {
                playerData.setTier(tier);
                playerData.setLatestTest(LocalDateTime.now().format(Tiers.formatter));

                PlayerData testerData = new PlayerData(playerMenuUtil.getOwner().getUniqueId());
                if (testerData.dataExists() && testerData.isTester()) {
                    testerData.setTests(testerData.getTests() + 1);
                    testerData.setLatestTestOther(LocalDateTime.now().format(Tiers.formatter));
                }

                playerMenuUtil.getOwner().sendMessage(ConfigManager.getConfigMessage(playerMenuUtil.getTargetPlayer(), "tier_set_message"));
                playerMenuUtil.getOwner().closeInventory();
            }
            case "CANCEL" -> {
                playerMenuUtil.getOwner().sendMessage(ConfigManager.getConfigMessage(playerMenuUtil.getTargetPlayer(), "cancel_message"));
                playerMenuUtil.getOwner().closeInventory();
            }
            default -> {
                return;
            }
        }

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
    }

    @Override
    public void handleClose(InventoryCloseEvent e) {

    }

    @Override
    public void setMenuItems() {
        if (menuConfig.getBoolean("border", false))
            addMenuBorder();

        ConfigurationSection itemSection = menuConfig.getConfigurationSection("items");
        if (itemSection == null)
            return;

        itemSection.getKeys(false).forEach(key -> {
            ConfigurationSection keySection = itemSection.getConfigurationSection(key);
            ItemStack itemStack = ItemBuilder.fromConfig(keySection, playerMenuUtil.getTargetPlayer());
            inventory.setItem(Integer.parseInt(key), itemStack);
        });
    }
}
