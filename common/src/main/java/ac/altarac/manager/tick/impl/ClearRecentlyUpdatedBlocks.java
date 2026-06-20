package ac.altarac.manager.tick.impl;

import ac.altarac.AltarACAPI;
import ac.altarac.manager.tick.Tickable;
import ac.altarac.player.AltarACPlayer;

public class ClearRecentlyUpdatedBlocks implements Tickable {

    private static final int maxTickAge = 2;

    @Override
    public void tick() {
        for (AltarACPlayer player : AltarACAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.blockHistory.cleanup(AltarACAPI.INSTANCE.getTickManager().currentTick - maxTickAge);
        }
    }
}
