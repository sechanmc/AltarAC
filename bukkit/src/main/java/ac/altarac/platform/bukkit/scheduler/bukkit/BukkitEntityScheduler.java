package ac.altarac.platform.bukkit.scheduler.bukkit;

import ac.altarac.api.plugin.AltarACPlugin;
import ac.altarac.platform.api.entity.AltarACEntity;
import ac.altarac.platform.api.scheduler.EntityScheduler;
import ac.altarac.platform.api.scheduler.TaskHandle;
import ac.altarac.platform.bukkit.AltarACBukkitLoaderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitEntityScheduler implements EntityScheduler {
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    @Override
    public void execute(@NotNull AltarACEntity entity, @NotNull AltarACPlugin plugin, @NotNull Runnable run, @Nullable Runnable retired, long delay) {
        scheduler.runTaskLater(AltarACBukkitLoaderPlugin.LOADER, run, delay);
    }

    @Override
    public TaskHandle run(@NotNull AltarACEntity entity, @NotNull AltarACPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired) {
        return new BukkitTaskHandle(scheduler.runTask(AltarACBukkitLoaderPlugin.LOADER, task));
    }

    @Override
    public TaskHandle runDelayed(@NotNull AltarACEntity entity, @NotNull AltarACPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long delayTicks) {
        return new BukkitTaskHandle(scheduler.runTaskLater(AltarACBukkitLoaderPlugin.LOADER, task, delayTicks));
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull AltarACEntity entity, @NotNull AltarACPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long initialDelayTicks, long periodTicks) {
        return new BukkitTaskHandle(scheduler.runTaskTimer(AltarACBukkitLoaderPlugin.LOADER, task, initialDelayTicks, periodTicks));
    }
}
