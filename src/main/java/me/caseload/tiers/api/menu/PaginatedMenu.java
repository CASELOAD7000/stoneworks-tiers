package me.caseload.tiers.api.menu;

import me.caseload.tiers.api.util.PlayerMenuUtil;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected int maxItemsPerPage = 28;
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtil playerMenuUtil) {
        super(playerMenuUtil);
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}
