package ac.altarac.platform.fabric;

import ac.altarac.AltarACAPI;
import ac.altarac.api.AltarACAPIProvider;
import ac.altarac.api.plugin.AltarACPlugin;
import ac.altarac.command.CloudCommandService;
import ac.altarac.internal.plugin.resolver.AltarACExtensionManager;
import ac.altarac.platform.api.PlatformLoader;
import ac.altarac.platform.api.PlatformServer;
import ac.altarac.platform.api.command.CommandService;
import ac.altarac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.altarac.platform.api.manager.ItemResetHandler;
import ac.altarac.platform.api.manager.MessagePlaceHolderManager;
import ac.altarac.platform.api.manager.PermissionRegistrationManager;
import ac.altarac.platform.api.manager.PlatformPluginManager;
import ac.altarac.platform.api.player.PlatformPlayerFactory;
import ac.altarac.platform.api.scheduler.PlatformScheduler;
import ac.altarac.platform.api.sender.Sender;
import ac.altarac.platform.api.sender.SenderFactory;
import ac.altarac.platform.fabric.manager.FabricMessagePlaceHolderManager;
import ac.altarac.platform.fabric.manager.FabricPlatformPluginManager;
import ac.altarac.platform.fabric.resolver.FabricResolverRegistrar;
import ac.altarac.platform.fabric.utils.convert.IFabricConversionUtil;
import ac.altarac.platform.fabric.utils.message.IFabricMessageUtil;
import ac.altarac.utils.anticheat.LogUtil;
import ac.altarac.utils.lazy.LazyHolder;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import lombok.Getter;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.fabric.FabricServerCommandManager;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractAltarACFabricLoaderPlugin<
        P extends PlatformPlayerFactory,
        S extends PlatformServer,
        H extends PlatformScheduler,
        F extends SenderFactory<?>,
        I extends ItemResetHandler,
        A extends CloudPlatformCommandArguments
        > implements PlatformLoader {

    protected final LazyHolder<H> scheduler;
    protected final PacketEventsAPI<?> packetEvents = PacketEvents.getAPI();
    protected final LazyHolder<F> senderFactory;
    protected final LazyHolder<I> itemResetHandler;
    protected final LazyHolder<A> commandArguments;
    protected final LazyHolder<CommandService> commandService = LazyHolder.simple(this::createCommandService);
    protected final LazyHolder<? extends PermissionRegistrationManager> permissionManager;
    protected final AltarACPlugin plugin;
    @Getter
    protected final PlatformPluginManager pluginManager = new FabricPlatformPluginManager();
    @Getter
    protected final MessagePlaceHolderManager messagePlaceHolderManager = new FabricMessagePlaceHolderManager();
    protected final P playerFactory;
    protected final S platformServer;
    protected final IFabricMessageUtil fabricMessageUtil;
    @Getter
    protected final IFabricConversionUtil fabricConversionUtil;

    protected AbstractAltarACFabricLoaderPlugin(
            LazyHolder<H> scheduler,
            LazyHolder<F> senderFactory,
            LazyHolder<I> itemResetHandler,
            LazyHolder<A> commandArguments,
            LazyHolder<? extends PermissionRegistrationManager> permissionManager,
            P playerFactory,
            S platformServer,
            IFabricMessageUtil fabricMessageUtil,
            IFabricConversionUtil fabricConversionUtil
    ) {
        this.scheduler = scheduler;
        this.senderFactory = senderFactory;
        this.itemResetHandler = itemResetHandler;
        this.commandArguments = commandArguments;
        this.permissionManager = permissionManager;
        this.playerFactory = playerFactory;
        this.platformServer = platformServer;
        this.fabricMessageUtil = fabricMessageUtil;
        this.fabricConversionUtil = fabricConversionUtil;

        FabricResolverRegistrar resolverRegistrar = new FabricResolverRegistrar();
        AltarACExtensionManager extensionManager = AltarACAPI.INSTANCE.getExtensionManager();
        resolverRegistrar.registerAll(extensionManager);
        plugin = extensionManager.getPlugin("AltarAC");
    }

    @Override
    public H getScheduler() {
        return scheduler.get();
    }

    @Override
    public PacketEventsAPI<?> getPacketEvents() {
        return packetEvents;
    }

    @Override
    public I getItemResetHandler() {
        return itemResetHandler.get();
    }

    @Override
    public CommandService getCommandService() {
        return commandService.get();
    }

    @Override
    public SenderFactory<?> getSenderFactory() {
        return senderFactory.get();
    }

    @Override
    public AltarACPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void registerAPIService() {
        AltarACAPIProvider.init(AltarACAPI.INSTANCE.getExternalAPI());
    }

    @Override
    public PermissionRegistrationManager getPermissionManager() {
        return permissionManager.get();
    }

    @Override
    public P getPlatformPlayerFactory() {
        return playerFactory;
    }

    @Override
    public S getPlatformServer() {
        return platformServer;
    }

    public IFabricMessageUtil getFabricMessageUtils() {
        return fabricMessageUtil;
    }

    private CommandService createCommandService() {
        try {
            return createPlatformCommandService();
        } catch (Throwable t) {
            LogUtil.warn("IMPORTANT: Command Framework failed to load (Missing Cloud Library?). \n" +
                    "AltarAC will run without commands enabled!");
            if (!(t instanceof NoClassDefFoundError)) {
                t.printStackTrace();
            }
            return () -> {};
        }
    }

    protected CommandService createPlatformCommandService() {
        @SuppressWarnings({"rawtypes", "unchecked"})
        CommandManager<@NotNull Sender> manager = new FabricServerCommandManager(
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );
        return new CloudCommandService(() -> manager, commandArguments.get());
    }

    public abstract ServerVersion getNativeVersion();
}
