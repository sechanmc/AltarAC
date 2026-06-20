package ac.altarac.platform.bukkit.scheduler.bukkit;

import ac.altarac.api.plugin.AltarACPlugin;
import ac.altarac.platform.api.scheduler.GlobalRegionScheduler;
import ac.altarac.platform.api.scheduler.TaskHandle;
import ac.altarac.platform.bukkit.AltarACBukkitLoaderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public class BukkitGlobalRegionScheduler implements GlobalRegionScheduler {

    private final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

    @Override
    public void execute(@NotNull AltarACPlugin plugin, @NotNull Runnable task) {
        bukkitScheduler.runTask(AltarACBukkitLoaderPlugin.LOADER, task);
    }

    @Override
    public TaskHandle run(@NotNull AltarACPlugin plugin, @NotNull Runnable task) {
        return new BukkitTaskHandle(bukkitScheduler.runTask(AltarACBukkitLoaderPlugin.LOADER, task));
    }

    @Override
    public TaskHandle runDelayed(@NotNull AltarACPlugin plugin, @NotNull Runnable task, long delay) {
        return new BukkitTaskHandle(bukkitScheduler.runTaskLater(AltarACBukkitLoaderPlugin.LOADER, task, delay));
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull AltarACPlugin plugin, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
        return new BukkitTaskHandle(bukkitScheduler.runTaskTimer(AltarACBukkitLoaderPlugin.LOADER, task, initialDelayTicks, periodTicks));
    }

    @Override
    public void cancel(@NotNull AltarACPlugin plugin) {
        bukkitScheduler.cancelTasks(AltarACBukkitLoaderPlugin.LOADER);
    }
}
