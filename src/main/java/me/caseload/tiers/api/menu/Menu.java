package me.caseload.tiers.api.menu;

import me.caseload.tiers.api.util.ItemBuilder;
import me.caseload.tiers.api.util.PlayerMenuUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu implements InventoryHolder {

    protected PlayerMenuUtil playerMenuUtil;
    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
            .displayName(" ")
            .setCustomModelData(1)
            .asItemStack();

    public Menu(PlayerMenuUtil playerMenuUtil) {
        this.playerMenuUtil = playerMenuUtil;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleClick(InventoryClickEvent e);
    public abstract void handleClose(InventoryCloseEvent e);

    public abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();

        playerMenuUtil.getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void fillMenu(){
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null){
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }

    public void addMenuBorder() {
        int slots = getSlots();
        int lastRowStart = slots - 9;
        List<ItemStack> modifiedItems = new ArrayList<>();

        for (int i = 0; i < slots; i++) {
            if (i < 9 || i >= lastRowStart || i % 9 == 0 || i % 9 == 8) {
                if (inventory.getItem(i) == null) {
                    modifiedItems.add(FILLER_GLASS);
                } else {
                    modifiedItems.add(inventory.getItem(i));
                }
            } else {
                modifiedItems.add(inventory.getItem(i));
            }
        }

        inventory.setContents(modifiedItems.toArray(new ItemStack[0]));
    }
}

