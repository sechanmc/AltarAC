package ac.altarac.manager.init.start;

import ac.altarac.AltarACAPI;
import ac.altarac.player.AltarACPlayer;

// Intended for future events we inject all platforms at the end of a tick
public abstract class AbstractTickEndEvent implements StartableInitable {

    @Override
    public void start() {

    }

    protected void onEndOfTick(AltarACPlayer player, boolean flush) {
        player.checkManager.getPacketEntityReplication().onEndOfTickEvent(true, flush);
    }

    protected boolean shouldInjectEndTick() {
        return AltarACAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("Reach.enable-post-packet", false);
    }
}
