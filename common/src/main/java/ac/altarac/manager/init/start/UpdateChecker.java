package ac.altarac.manager.init.start;

import ac.altarac.AltarACAPI;
import ac.altarac.command.commands.AltarACVersion;

public class UpdateChecker implements StartableInitable {
    @Override
    public void start() {
        if (AltarACAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("check-for-updates", true)) {
            AltarACVersion.checkForUpdatesAsync(AltarACAPI.INSTANCE.getPlatformServer().getConsoleSender());
        }
    }
}
