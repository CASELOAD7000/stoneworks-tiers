package me.caseload.tiers.listener;

import me.caseload.tiers.api.menu.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!isMenu(holder))
            return;

        Menu menu = (Menu) holder;
        menu.handleClick(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!isMenu(holder))
            return;

        Menu menu = (Menu) holder;
        menu.handleClose(event);
    }

    private boolean isMenu(InventoryHolder holder) {
        return holder instanceof Menu;
    }
}
