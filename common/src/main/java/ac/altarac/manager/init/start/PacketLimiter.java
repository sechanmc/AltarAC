package ac.altarac.manager.init.start;

import ac.altarac.AltarACAPI;
import ac.altarac.player.AltarACPlayer;

public class PacketLimiter implements StartableInitable {
    @Override
    public void start() {
        AltarACAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(AltarACAPI.INSTANCE.getPlugin(), () -> {
            for (AltarACPlayer player : AltarACAPI.INSTANCE.getPlayerDataManager().getEntries()) {
                // Avoid concurrent reading on an integer as it's results are unknown
                player.cancelledPackets.set(0);
            }
        }, 1, 20);
    }
}
