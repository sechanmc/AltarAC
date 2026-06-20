package ac.altarac.command.commands;

import ac.altarac.AltarACAPI;
import ac.altarac.command.BuildableCommand;
import ac.altarac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.altarac.platform.api.sender.Sender;
import ac.altarac.utils.anticheat.LogUtil;
import ac.altarac.utils.anticheat.MessageUtil;
import ac.altarac.utils.data.webhook.discord.WebhookMessage;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

public class AltarACTestWebhook implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        commandManager.command(
                commandManager.commandBuilder("AltarAC", "AltarAC")
                        .literal("testwebhook")
                        .permission("AltarAC.testwebhook")
                        .handler(this::handleTestWebhook)
        );
    }

    private void handleTestWebhook(@NotNull CommandContext<Sender> context) {
        if (AltarACAPI.INSTANCE.getDiscordManager().isDisabled()) {
            context.sender().sendMessage(MessageUtil.miniMessage(AltarACAPI.INSTANCE.getConfigManager().getWebhookNotEnabled()));
            return;
        }

        WebhookMessage webhookMessage = new WebhookMessage().content(AltarACAPI.INSTANCE.getConfigManager().getWebhookTestMessage());
        AltarACAPI.INSTANCE.getDiscordManager().sendWebhookMessage(webhookMessage).whenCompleteAsync(((successful, throwable) -> {
            if (successful == true) {
                context.sender().sendMessage(MessageUtil.miniMessage(AltarACAPI.INSTANCE.getConfigManager().getWebhookTestSucceeded()));
                return;
            }

            context.sender().sendMessage(MessageUtil.miniMessage(AltarACAPI.INSTANCE.getConfigManager().getWebhookTestFailed()));

            if (throwable != null) {
                LogUtil.error("Exception caught while sending a Discord webhook test alert", throwable);
            }
        }));
    }
}
