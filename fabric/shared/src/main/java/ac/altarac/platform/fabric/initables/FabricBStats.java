package ac.altarac.platform.fabric.initables;

import ac.altarac.AltarACAPI;
import ac.altarac.manager.init.start.StartableInitable;
import ac.altarac.manager.init.stop.StoppableInitable;
import ac.altarac.platform.fabric.utils.metrics.MetricsFabric;
import ac.altarac.utils.anticheat.Constants;

public class FabricBStats implements StartableInitable, StoppableInitable {

    private MetricsFabric metricsFabric;

    @Override
    public void start() {
        try {
            metricsFabric = new MetricsFabric(AltarACAPI.INSTANCE.getPlugin(), Constants.BSTATS_PLUGIN_ID);
        } catch (Exception ignored) {}
    }

    @Override
    public void stop() {
        if (metricsFabric != null)
            metricsFabric.shutdown();
    }
}
