package ac.altarac.platform.bukkit.scheduler.folia;

import ac.altarac.api.plugin.AltarACPlugin;
import ac.altarac.platform.api.scheduler.GlobalRegionScheduler;
import ac.altarac.platform.api.scheduler.TaskHandle;
import ac.altarac.platform.bukkit.AltarACBukkitLoaderPlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class FoliaGlobalRegionScheduler implements GlobalRegionScheduler {

    private final io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler globalRegionScheduler = Bukkit.getGlobalRegionScheduler();

    @Override
    public void execute(@NotNull AltarACPlugin plugin, @NotNull Runnable task) {
        globalRegionScheduler.execute(AltarACBukkitLoaderPlugin.LOADER, task);
    }

    @Override
    public TaskHandle run(@NotNull AltarACPlugin plugin, @NotNull Runnable task) {
        return new FoliaTaskHandle(globalRegionScheduler.run(AltarACBukkitLoaderPlugin.LOADER, ignored -> task.run()));
    }

    @Override
    public TaskHandle runDelayed(@NotNull AltarACPlugin plugin, @NotNull Runnable task, long delay) {
        return new FoliaTaskHandle(globalRegionScheduler.runDelayed(AltarACBukkitLoaderPlugin.LOADER, ignored -> task.run(), delay));
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull AltarACPlugin plugin, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
        return new FoliaTaskHandle(globalRegionScheduler.runAtFixedRate(AltarACBukkitLoaderPlugin.LOADER, ignored -> task.run(), initialDelayTicks, periodTicks));
    }

    @Override
    public void cancel(@NotNull AltarACPlugin plugin) {
        globalRegionScheduler.cancelTasks(AltarACBukkitLoaderPlugin.LOADER);
    }
}
