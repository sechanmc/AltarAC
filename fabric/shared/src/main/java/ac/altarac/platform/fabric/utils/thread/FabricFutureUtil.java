package ac.altarac.platform.fabric.utils.thread;

import ac.altarac.AltarACAPI;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class FabricFutureUtil {
    public static <U> CompletableFuture<U> supplySync(Supplier<U> entityTeleportSupplier) {
        CompletableFuture<U> ret = new CompletableFuture<>();
        AltarACAPI.INSTANCE.getScheduler().getGlobalRegionScheduler().run(AltarACAPI.INSTANCE.getPlugin(),
                () -> ret.complete(entityTeleportSupplier.get()));
        return ret;
    }
}
