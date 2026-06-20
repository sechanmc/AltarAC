package ac.altarac.platform.bukkit.initables;

import ac.altarac.manager.init.start.StartableInitable;
import ac.altarac.platform.bukkit.AltarACBukkitLoaderPlugin;
import ac.altarac.utils.anticheat.Constants;
import io.github.retrooper.packetevents.bstats.bukkit.Metrics;

public class BukkitBStats implements StartableInitable {
    @Override
    public void start() {
        try {
            new Metrics(AltarACBukkitLoaderPlugin.LOADER, Constants.BSTATS_PLUGIN_ID);
        } catch (Exception ignored) {}
    }
}
