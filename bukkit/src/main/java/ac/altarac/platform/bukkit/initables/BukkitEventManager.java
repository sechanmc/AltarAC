package ac.altarac.platform.bukkit.initables;

import ac.altarac.manager.init.start.StartableInitable;
import ac.altarac.platform.bukkit.AltarACBukkitLoaderPlugin;
import ac.altarac.platform.bukkit.events.PistonEvent;
import ac.altarac.utils.anticheat.LogUtil;
import org.bukkit.Bukkit;

public class BukkitEventManager implements StartableInitable {
    public void start() {
        LogUtil.info("Registering singular bukkit event... (PistonEvent)");

        Bukkit.getPluginManager().registerEvents(new PistonEvent(), AltarACBukkitLoaderPlugin.LOADER);
    }
}
