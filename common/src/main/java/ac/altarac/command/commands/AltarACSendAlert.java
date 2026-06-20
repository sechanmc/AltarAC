package ac.altarac.command.commands;

import ac.altarac.AltarACAPI;
import ac.altarac.command.BuildableCommand;
import ac.altarac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.altarac.platform.api.sender.Sender;
import ac.altarac.utils.anticheat.MessageUtil;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;

public class AltarACSendAlert implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        commandManager.command(
                commandManager.commandBuilder("AltarAC", "AltarAC")
                        .literal("sendalert")
                        .permission("AltarAC.sendalert")
                        .required("message", StringParser.greedyStringParser())
                        .handler(this::handleSendAlert)
        );
    }

    private void handleSendAlert(@NotNull CommandContext<Sender> context) {
        String string = context.get("message");
        string = MessageUtil.replacePlaceholders((Sender) null, string);
        Component message = MessageUtil.miniMessage(string);
        AltarACAPI.INSTANCE.getAlertManager().sendAlert(message, null);
    }
}
