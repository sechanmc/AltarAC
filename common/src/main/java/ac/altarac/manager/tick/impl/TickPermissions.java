package ac.altarac.manager.tick.impl;

import ac.altarac.AltarACAPI;
import ac.altarac.manager.config.BaseConfigManager;
import ac.altarac.manager.tick.Tickable;
import ac.altarac.player.AltarACPlayer;

public class TickPermissions implements Tickable {

    @Override
    public void tick() {
        BaseConfigManager config = AltarACAPI.INSTANCE.getConfigManager();
        int interval = config.getUpdatePermissionTicks();
        if (interval <= 0 || AltarACAPI.INSTANCE.getTickManager().currentTick % interval != 0) return;

        for (AltarACPlayer player : AltarACAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.updatePermissions();
        }
    }
}
