package ac.altarac.api.plugin;

import java.io.File;
import java.util.logging.Logger;

public interface AltarACPlugin {

    AltarACPluginDescription getDescription();

    Logger getLogger();

    File getDataFolder();
}
