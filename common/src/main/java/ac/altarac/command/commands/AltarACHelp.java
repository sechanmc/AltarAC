package ac.altarac.command.commands;

import ac.altarac.AltarACAPI;
import ac.altarac.command.BuildableCommand;
import ac.altarac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.altarac.platform.api.sender.Sender;
import ac.altarac.utils.anticheat.MessageUtil;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

public class AltarACHelp implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        commandManager.command(
                commandManager.commandBuilder("AltarAC", "AltarAC")
                        .literal("help", Description.of("Display help information"))
                        .permission("AltarAC.help")
                        .handler(this::handleHelp)
        );
    }

    private void handleHelp(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();

        for (String string : AltarACAPI.INSTANCE.getConfigManager().getConfig().getStringList("help")) {
            if (string == null) continue;
            string = MessageUtil.replacePlaceholders(sender, string);
            sender.sendMessage(MessageUtil.miniMessage(string));
        }
    }
}
