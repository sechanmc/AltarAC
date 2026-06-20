package ac.altarac.manager.tick.impl;

import ac.altarac.AltarACAPI;
import ac.altarac.manager.tick.Tickable;
import ac.altarac.player.AltarACPlayer;

public class TickInventory implements Tickable {
    @Override
    public void tick() {
        for (AltarACPlayer player : AltarACAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.inventory.inventory.getInventoryStorage().tickWithBukkit();
        }
    }
}
