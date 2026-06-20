package ac.altarac.checks;

import ac.altarac.AltarACAPI;
import ac.altarac.api.AbstractProcessor;
import ac.altarac.api.config.ConfigReloadable;
import ac.altarac.utils.common.ConfigReloadObserver;

public abstract class AltarACProcessor implements AbstractProcessor, ConfigReloadable, ConfigReloadObserver {

    // Not everything has to be a check for it to process packets & be configurable

    @Override
    public void reload() {
        reload(AltarACAPI.INSTANCE.getConfigManager().getConfig());
    }

}
