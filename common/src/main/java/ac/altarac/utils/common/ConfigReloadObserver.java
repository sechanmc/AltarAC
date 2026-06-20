package ac.altarac.utils.common;


import ac.altarac.api.config.ConfigManager;

public interface ConfigReloadObserver {

    void onReload(ConfigManager config);

}
