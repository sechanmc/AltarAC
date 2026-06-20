package ac.altarac.manager.init.start;

import ac.altarac.platform.api.command.CommandService;
import ac.altarac.utils.anticheat.LogUtil;

public record CommandRegister(CommandService service) implements StartableInitable {

    @Override
    public void start() {
        try {
            if (service != null) {
                service.registerCommands();
            }
        } catch (Throwable t) {
            // This is the ultimate safety net. If command registration fails, AltarAC keeps running.
            LogUtil.error("Failed to register commands! AltarAC will run without command support.", t);
        }
    }
}
