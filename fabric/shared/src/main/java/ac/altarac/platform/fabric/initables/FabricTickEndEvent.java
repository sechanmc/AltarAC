package ac.altarac.platform.fabric.initables;

import ac.altarac.AltarACAPI;
import ac.altarac.manager.init.start.AbstractTickEndEvent;
import ac.altarac.platform.fabric.FabricServerEvents;
import ac.altarac.player.AltarACPlayer;

public class FabricTickEndEvent extends AbstractTickEndEvent {

    @Override
    public void start() {
        if (!super.shouldInjectEndTick()) {
            return;
        }

        FabricServerEvents.onEndTick(server -> tickAllPlayers());
    }

    private void tickAllPlayers() {
        for (AltarACPlayer player : AltarACAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            if (player.disablePlugin) continue;
            super.onEndOfTick(player, true);
        }
    }
}
