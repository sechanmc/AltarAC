package ac.altarac.platform.fabric;

import ac.altarac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.altarac.platform.api.sender.SenderFactory;
import ac.altarac.platform.fabric.manager.FabricItemResetHandler;
import ac.altarac.platform.fabric.command.FabricPlayerSelectorParser;
import ac.altarac.platform.fabric.manager.FabricCloudPlatformCommandArguments;
import ac.altarac.platform.fabric.manager.FabricPermissionRegistrationManager;
import ac.altarac.platform.fabric.player.FabricPlatformPlayerFactory;
import ac.altarac.platform.fabric.scheduler.FabricPlatformScheduler;
import ac.altarac.platform.fabric.sender.AbstractFabricSenderFactory;
import ac.altarac.platform.fabric.sender.FabricIntermediarySenderFactory;
import me.lucko.fabric.api.permissions.v0.Permissions;
import ac.altarac.platform.fabric.utils.FabricIntermediaryPolymerHook;
import ac.altarac.platform.fabric.utils.convert.IFabricConversionUtil;
import ac.altarac.platform.fabric.utils.message.IFabricMessageUtil;
import ac.altarac.utils.lazy.LazyHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public abstract class AltarACFabricIntermediaryLoaderPlugin extends AbstractAltarACFabricLoaderPlugin<
        FabricPlatformPlayerFactory,
        AbstractFabricPlatformServer,
        FabricPlatformScheduler,
        FabricIntermediarySenderFactory,
        FabricItemResetHandler,
        CloudPlatformCommandArguments
        > {
    public static MinecraftServer FABRIC_SERVER;
    public static AltarACFabricIntermediaryLoaderPlugin LOADER;

    public AltarACFabricIntermediaryLoaderPlugin(
            LazyHolder<CloudPlatformCommandArguments> commandArguments,
            FabricPlatformPlayerFactory playerFactory,
            AbstractFabricPlatformServer platformServer,
            IFabricMessageUtil fabricMessageUtil,
            IFabricConversionUtil fabricConversionUtil
    ) {
        super(
                LazyHolder.simple(FabricPlatformScheduler::new),
                LazyHolder.simple(FabricIntermediarySenderFactory::new),
                LazyHolder.simple(() -> new FabricItemResetHandler(fabricConversionUtil)),
                commandArguments,
                LazyHolder.simple(() -> new FabricPermissionRegistrationManager(
                        LOADER.getFabricSenderFactory(),
                        name -> {
                            if (AbstractFabricSenderFactory.HAS_PERMISSIONS_API) {
                                Permissions.check(FABRIC_SERVER.createCommandSourceStack(), name);
                            }
                        })),
                playerFactory,
                platformServer,
                fabricMessageUtil,
                fabricConversionUtil
        );
        FabricPlatformServices.configure(
                playerFactory::getPlatformInventory,
                playerFactory::getPlatformEntity,
                player -> FabricIntermediaryPolymerHook.createTranslator((ServerPlayer) player),
                fabricMessageUtil::textLiteral,
                platformServer::getProfileByName,
                fabricConversionUtil
        );
    }

    @Override
    public SenderFactory<CommandSourceStack> getSenderFactory() {
        return senderFactory.get();
    }

    public FabricIntermediarySenderFactory getFabricSenderFactory() {
        return senderFactory.get();
    }

    public static FabricCloudPlatformCommandArguments createCommandArguments() {
        return new FabricCloudPlatformCommandArguments(new FabricPlayerSelectorParser<>(
                selector -> LOADER.getFabricSenderFactory().wrap(selector.single().createCommandSourceStack()),
                selector -> selector.inputString()
        ));
    }

}
