package ac.altarac.manager.init.start;

import ac.altarac.AltarACAPI;
import ac.altarac.platform.api.Platform;
import ac.altarac.utils.anticheat.LogUtil;

public class TickRunner implements StartableInitable {
    @Override
    public void start() {
        LogUtil.info("Registering tick schedulers...");

        if (AltarACAPI.INSTANCE.getPlatform() == Platform.FOLIA) {
            AltarACAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(AltarACAPI.INSTANCE.getPlugin(), () -> {
                AltarACAPI.INSTANCE.getTickManager().tickSync();
                AltarACAPI.INSTANCE.getTickManager().tickAsync();
            }, 1, 1);
        } else {
            AltarACAPI.INSTANCE.getScheduler().getGlobalRegionScheduler().runAtFixedRate(AltarACAPI.INSTANCE.getPlugin(), () -> AltarACAPI.INSTANCE.getTickManager().tickSync(), 0, 1);
            AltarACAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(AltarACAPI.INSTANCE.getPlugin(), () -> AltarACAPI.INSTANCE.getTickManager().tickAsync(), 0, 1);
        }
    }
}
