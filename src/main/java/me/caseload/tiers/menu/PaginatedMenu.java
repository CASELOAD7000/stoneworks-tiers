package me.caseload.tiers.menu;

import me.caseload.tiers.api.PlayerData;
import me.caseload.tiers.api.util.ItemBuilder;
import me.caseload.tiers.api.util.PlayerMenuUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PaginatedMenu extends me.caseload.tiers.api.menu.PaginatedMenu {

    private final FileConfiguration menuConfig;
    private final List<PlayerData> keys;
    private final int totalPages;
    private final int lastPage;

    public PaginatedMenu(PlayerMenuUtil playerMenuUtil, FileConfiguration menuConfig, List<PlayerData> keys) {
        super(playerMenuUtil);
        this.menuConfig = menuConfig;
        this.keys = keys;
        this.totalPages = (int) Math.ceil(keys.size() / (double) maxItemsPerPage);
        this.lastPage = totalPages - 1;
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
    }

    @Override
    public void handleClose(InventoryCloseEvent e) {

    }

    @Override
    public void setMenuItems() {
        if (menuConfig.getBoolean("border", false))
            addMenuBorder();

        ConfigurationSection itemSection = menuConfig.getConfigurationSection("items");
        if (itemSection != null) {
            itemSection.getKeys(false).forEach(key -> {
                if (key.equalsIgnoreCase("paginated_item"))
                    return;

                ConfigurationSection keySection = itemSection.getConfigurationSection(key);

                String action = keySection.getString("action");
                if ((action.equalsIgnoreCase("PREVIOUS_PAGE")
                        || (action.equalsIgnoreCase("NEXT_PAGE"))
                        && (page == 0 || keys.isEmpty())))
                    return;

                ItemStack itemStack = ItemBuilder.fromConfig(keySection, playerMenuUtil.getOwner());
                inventory.setItem(Integer.parseInt(key), itemStack);
            });
        }

        ConfigurationSection playerSection = itemSection.getConfigurationSection("paginated_item");
        if (playerSection == null)
            return;

        for (int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * page + i;
            if (index >= keys.size())
                break;

            PlayerData playerData = keys.get(index);
            if (!playerData.isTiered())
                continue;

            OfflinePlayer player = playerData.getOfflinePlayer();
            inventory.addItem(ItemBuilder.fromConfig(playerSection, player));
        }
    }
}
