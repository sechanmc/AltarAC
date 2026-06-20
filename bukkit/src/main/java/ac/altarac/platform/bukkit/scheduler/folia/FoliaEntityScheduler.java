package ac.altarac.platform.bukkit.scheduler.folia;

import ac.altarac.api.plugin.AltarACPlugin;
import ac.altarac.platform.api.entity.AltarACEntity;
import ac.altarac.platform.api.scheduler.EntityScheduler;
import ac.altarac.platform.api.scheduler.TaskHandle;
import ac.altarac.platform.bukkit.AltarACBukkitLoaderPlugin;
import ac.altarac.platform.bukkit.entity.BukkitAltarACEntity;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoliaEntityScheduler implements EntityScheduler {

    @Override
    public void execute(@NotNull AltarACEntity entity, @NotNull AltarACPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long delay) {
        ((BukkitAltarACEntity) entity).getBukkitEntity().getScheduler().execute(AltarACBukkitLoaderPlugin.LOADER, task, retired, delay);
    }

    @Override
    public TaskHandle run(@NotNull AltarACEntity entity, @NotNull AltarACPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired) {
        ScheduledTask scheduled = ((BukkitAltarACEntity) entity).getBukkitEntity().getScheduler().run(
                AltarACBukkitLoaderPlugin.LOADER,
                ignored -> task.run(),
                retired
        );

        return scheduled == null ? null : new FoliaTaskHandle(scheduled);
    }

    @Override
    public TaskHandle runDelayed(@NotNull AltarACEntity entity, @NotNull AltarACPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long delayTicks) {
        ScheduledTask scheduled = ((BukkitAltarACEntity) entity).getBukkitEntity().getScheduler().runDelayed(
                AltarACBukkitLoaderPlugin.LOADER,
                ignored -> task.run(),
                retired,
                delayTicks
        );

        return scheduled == null ? null : new FoliaTaskHandle(scheduled);
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull AltarACEntity entity, @NotNull AltarACPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long initialDelayTicks, long periodTicks) {
        ScheduledTask scheduled = ((BukkitAltarACEntity) entity).getBukkitEntity().getScheduler().runAtFixedRate(
                AltarACBukkitLoaderPlugin.LOADER,
                ignored -> task.run(),
                retired,
                initialDelayTicks,
                periodTicks
        );

        return scheduled == null ? null : new FoliaTaskHandle(scheduled);
    }
}
