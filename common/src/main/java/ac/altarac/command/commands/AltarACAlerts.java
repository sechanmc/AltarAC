package ac.altarac.command.commands;

import ac.altarac.AltarACAPI;
import ac.altarac.command.BuildableCommand;
import ac.altarac.manager.AlertManagerImpl;
import ac.altarac.manager.datastore.PlayerToggleStore;
import ac.altarac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.altarac.platform.api.player.PlatformPlayer;
import ac.altarac.platform.api.sender.Sender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AltarACAlerts implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        commandManager.command(
                commandManager.commandBuilder("AltarAC", "AltarAC")
                        .literal("alerts", Description.of("Toggle alerts for the sender"))
                        .permission("AltarAC.alerts")
                        .handler(this::handleAlerts)
        );
    }

    private void handleAlerts(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        if (sender.isPlayer()) {
            PlatformPlayer p = Objects.requireNonNull(context.sender().getPlatformPlayer());
            AlertManagerImpl am = AltarACAPI.INSTANCE.getAlertManager();
            boolean newState = !am.hasAlertsEnabled(p);
            am.setAlertsEnabled(p, newState, false);
            AltarACAPI.INSTANCE.getDataStoreLifecycle().playerToggleStore()
                    .applyUserToggle(p.getUniqueId(), PlayerToggleStore.KEY_ALERTS, newState);
        } else if (sender.isConsole()) {
            AltarACAPI.INSTANCE.getAlertManager().toggleConsoleAlerts();
        }
    }
}
