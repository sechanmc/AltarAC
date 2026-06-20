package ac.altarac.platform.fabric;

import ac.altarac.platform.fabric.manager.FabricItemResetHandler;
import ac.altarac.platform.fabric.manager.FabricCloudPlatformCommandArguments;
import ac.altarac.platform.fabric.manager.FabricPermissionRegistrationManager;
import ac.altarac.platform.fabric.command.FabricPlayerSelectorParser;
import ac.altarac.platform.fabric.player.FabricPlatformPlayerFactory;
import ac.altarac.platform.fabric.scheduler.FabricPlatformScheduler;
import ac.altarac.platform.fabric.sender.AbstractFabricSenderFactory;
import ac.altarac.platform.fabric.sender.FabricOfficialSenderFactory;
import me.lucko.fabric.api.permissions.v0.Permissions;
import ac.altarac.platform.fabric.utils.FabricOfficialPolymerHook;
import ac.altarac.platform.fabric.utils.convert.IFabricConversionUtil;
import ac.altarac.platform.fabric.utils.message.IFabricMessageUtil;
import ac.altarac.utils.lazy.LazyHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public abstract class AltarACFabricOfficialLoaderPlugin extends AbstractAltarACFabricLoaderPlugin<
        FabricPlatformPlayerFactory,
        AbstractFabricPlatformServer,
        FabricPlatformScheduler,
        FabricOfficialSenderFactory,
        FabricItemResetHandler,
        FabricCloudPlatformCommandArguments
        > {
    public static MinecraftServer FABRIC_SERVER;
    public static AltarACFabricOfficialLoaderPlugin LOADER;

    public AltarACFabricOfficialLoaderPlugin(
            FabricPlatformPlayerFactory playerFactory,
            AbstractFabricPlatformServer platformServer,
            IFabricMessageUtil fabricMessageUtil,
            IFabricConversionUtil fabricConversionUtil
    ) {
        super(
                LazyHolder.simple(FabricPlatformScheduler::new),
                LazyHolder.simple(FabricOfficialSenderFactory::new),
                LazyHolder.simple(() -> new FabricItemResetHandler(fabricConversionUtil)),
                LazyHolder.simple(AltarACFabricOfficialLoaderPlugin::createCommandArguments),
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
                player -> FabricOfficialPolymerHook.createTranslator((ServerPlayer) player),
                fabricMessageUtil::textLiteral,
                platformServer::getProfileByName,
                fabricConversionUtil
        );
    }

    public FabricOfficialSenderFactory getFabricSenderFactory() {
        return senderFactory.get();
    }

    public static FabricCloudPlatformCommandArguments createCommandArguments() {
        return new FabricCloudPlatformCommandArguments(new FabricPlayerSelectorParser<>(
                selector -> LOADER.getFabricSenderFactory().wrap(selector.single().createCommandSourceStack()),
                selector -> selector.inputString()
        ));
    }

}
