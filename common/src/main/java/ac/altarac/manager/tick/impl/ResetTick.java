package ac.altarac.manager.tick.impl;

import ac.altarac.AltarACAPI;
import ac.altarac.manager.tick.Tickable;
import ac.altarac.player.AltarACPlayer;

public class ResetTick implements Tickable {
    @Override
    public void tick() {
        for (AltarACPlayer player : AltarACAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.checkManager.getPacketEntityReplication().tickStartTick();
        }
    }
}
