package ac.altarac.platform.bukkit;

import ac.altarac.AltarACAPI;
import ac.altarac.AltarACExternalAPI;
import ac.altarac.api.AltarACAPIProvider;
import ac.altarac.api.AltarACAbstractAPI;
import ac.altarac.api.event.EventBus;
import ac.altarac.api.plugin.AltarACPlugin;
import ac.altarac.command.CloudCommandService;
import ac.altarac.internal.platform.bukkit.resolver.BukkitResolverRegistrar;
import ac.altarac.manager.init.Initable;
import ac.altarac.manager.init.start.ExemptOnlinePlayersOnReload;
import ac.altarac.manager.init.start.StartableInitable;
import ac.altarac.platform.api.Platform;
import ac.altarac.platform.api.PlatformLoader;
import ac.altarac.platform.api.PlatformServer;
import ac.altarac.platform.api.command.CommandService;
import ac.altarac.platform.api.manager.ItemResetHandler;
import ac.altarac.platform.api.manager.MessagePlaceHolderManager;
import ac.altarac.platform.api.manager.PlatformPluginManager;
import ac.altarac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.altarac.platform.api.player.PlatformPlayerFactory;
import ac.altarac.platform.api.scheduler.PlatformScheduler;
import ac.altarac.platform.api.sender.Sender;
import ac.altarac.platform.api.sender.SenderFactory;
import ac.altarac.platform.bukkit.initables.BukkitBStats;
import ac.altarac.platform.bukkit.initables.BukkitEventManager;
import ac.altarac.platform.bukkit.initables.BukkitLuckPermsInitable;
import ac.altarac.platform.bukkit.initables.BukkitTickEndEvent;
import ac.altarac.platform.bukkit.manager.BukkitItemResetHandler;
import ac.altarac.platform.bukkit.manager.BukkitMessagePlaceHolderManager;
import ac.altarac.platform.bukkit.manager.BukkitCloudPlatformCommandArguments;
import ac.altarac.platform.bukkit.manager.BukkitPermissionRegistrationManager;
import ac.altarac.platform.bukkit.manager.BukkitPlatformPluginManager;
import ac.altarac.platform.bukkit.player.BukkitPlatformPlayerFactory;
import ac.altarac.platform.bukkit.scheduler.bukkit.BukkitPlatformScheduler;
import ac.altarac.platform.bukkit.scheduler.folia.FoliaPlatformScheduler;
import ac.altarac.platform.bukkit.sender.BukkitSenderFactory;
import ac.altarac.platform.bukkit.utils.placeholder.PlaceholderAPIExpansion;
import ac.altarac.utils.anticheat.LogUtil;
import ac.altarac.utils.lazy.LazyHolder;
import com.github.retrooper.packetevents.PacketEventsAPI;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.brigadier.BrigadierSetting;
import org.incendo.cloud.brigadier.CloudBrigadierManager;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public final class AltarACBukkitLoaderPlugin extends JavaPlugin implements PlatformLoader {
    public static AltarACBukkitLoaderPlugin LOADER;

    private final LazyHolder<PlatformScheduler> scheduler = LazyHolder.simple(this::createScheduler);
    private final LazyHolder<PacketEventsAPI<?>> packetEvents = LazyHolder.simple(() -> SpigotPacketEventsBuilder.build(this));
    private final LazyHolder<BukkitSenderFactory> senderFactory = LazyHolder.simple(BukkitSenderFactory::new);
    private final LazyHolder<ItemResetHandler> itemResetHandler = LazyHolder.simple(BukkitItemResetHandler::new);
    private final LazyHolder<CommandService> commandService = LazyHolder.simple(this::createCommandService);
    private final CloudPlatformCommandArguments commandArguments = new BukkitCloudPlatformCommandArguments();

    @Getter private final PlatformPlayerFactory platformPlayerFactory = new BukkitPlatformPlayerFactory();
    @Getter private final PlatformPluginManager pluginManager = new BukkitPlatformPluginManager();
    @Getter private final AltarACPlugin plugin;
    @Getter private final PlatformServer platformServer = new BukkitPlatformServer();
    @Getter private final MessagePlaceHolderManager messagePlaceHolderManager = new BukkitMessagePlaceHolderManager();
    @Getter private final BukkitPermissionRegistrationManager permissionManager = new BukkitPermissionRegistrationManager();

    public AltarACBukkitLoaderPlugin() {
        BukkitResolverRegistrar registrar = new BukkitResolverRegistrar();
        registrar.registerAll(AltarACAPI.INSTANCE.getExtensionManager());
        this.plugin = registrar.resolvePlugin(this);
    }

    @Override
    public void onLoad() {
        LOADER = this;
        AltarACAPI.INSTANCE.load(this, this.getBukkitInitTasks());
    }

    private Initable[] getBukkitInitTasks() {
        return new Initable[] {
                new ExemptOnlinePlayersOnReload(),
                new BukkitEventManager(),
                new BukkitTickEndEvent(),
                new BukkitBStats(),
                new BukkitLuckPermsInitable(),
                (StartableInitable) () -> {
                    if (BukkitMessagePlaceHolderManager.hasPlaceholderAPI) {
                        new PlaceholderAPIExpansion().register();
                    }
                }
        };
    }

    @Override
    public void onEnable() {
        AltarACAPI.INSTANCE.start();
    }

    @Override
    public void onDisable() {
        AltarACAPI.INSTANCE.stop();
    }

    @Override
    public PlatformScheduler getScheduler() {
        return scheduler.get();
    }

    @Override
    public PacketEventsAPI<?> getPacketEvents() {
        return packetEvents.get();
    }

    @Override
    public ItemResetHandler getItemResetHandler() {
        return itemResetHandler.get();
    }

    @Override
    public CommandService getCommandService() {
        return commandService.get();
    }

    @Override
    public SenderFactory<CommandSender> getSenderFactory() {
        return senderFactory.get();
    }

    @Override
    @SuppressWarnings("removal")
    public void registerAPIService() {
        final AltarACExternalAPI externalAPI = AltarACAPI.INSTANCE.getExternalAPI();
        final EventBus eventBus = externalAPI.getEventBus();
        final ac.altarac.api.plugin.AltarACPlugin plugin = AltarACAPI.INSTANCE.getPlugin();

        // Bridge AltarAC events → legacy Bukkit Event API so pre-1.3 plugins that
        // listened for ac.altarac.api.events.* Bukkit events keep working.
        // Typed channel subscriptions here are plugin-bound so they go away if
        // AltarAC itself is disabled.

        eventBus.get(ac.altarac.api.event.events.AltarACJoinEvent.class).onJoin(plugin, (user) -> {
            Bukkit.getPluginManager().callEvent(new ac.altarac.api.events.AltarACJoinEvent(user));
        });

        eventBus.get(ac.altarac.api.event.events.AltarACQuitEvent.class).onQuit(plugin, (user) -> {
            Bukkit.getPluginManager().callEvent(new ac.altarac.api.events.AltarACQuitEvent(user));
        });

        eventBus.get(ac.altarac.api.event.events.AltarACReloadEvent.class).onReload(plugin, (success) -> {
            Bukkit.getPluginManager().callEvent(new ac.altarac.api.events.AltarACReloadEvent(success));
        });

        eventBus.subscribe(plugin, ac.altarac.api.event.events.FlagEvent.class, event -> {
            ac.altarac.api.events.FlagEvent bukkitEvent =
                    new ac.altarac.api.events.FlagEvent(event.getUser(), event.getCheck(), event::getVerbose);
            Bukkit.getPluginManager().callEvent(bukkitEvent);
            event.setCancelled(event.isCancelled() || bukkitEvent.isCancelled());
        }, 0, false, AltarACBukkitLoaderPlugin.class);

        eventBus.get(ac.altarac.api.event.events.CommandExecuteEvent.class).onCommandExecute(plugin, (user, check, verbose, command, cancelled) -> {
            ac.altarac.api.events.CommandExecuteEvent bukkitEvent =
                    new ac.altarac.api.events.CommandExecuteEvent(user, check, verbose, command);
            Bukkit.getPluginManager().callEvent(bukkitEvent);
            return cancelled || bukkitEvent.isCancelled();
        });

        eventBus.get(ac.altarac.api.event.events.CompletePredictionEvent.class).onCompletePrediction(plugin, (user, check, offset, cancelled) -> {
            // Legacy Bukkit event has a verbose field that the new channel event does not; pass empty.
            ac.altarac.api.events.CompletePredictionEvent bukkitEvent =
                    new ac.altarac.api.events.CompletePredictionEvent(user, check, "", offset);
            Bukkit.getPluginManager().callEvent(bukkitEvent);
            return cancelled || bukkitEvent.isCancelled();
        });

        AltarACAPIProvider.init(externalAPI);
        Bukkit.getServicesManager().register(AltarACAbstractAPI.class, externalAPI, this, ServicePriority.Normal);
    }

    private PlatformScheduler createScheduler() {
        return AltarACAPI.INSTANCE.getPlatform() == Platform.FOLIA ? new FoliaPlatformScheduler() : new BukkitPlatformScheduler();
    }

    private CommandService createCommandService() {
        try {
            return new CloudCommandService(this::createCloudCommandManager, commandArguments);
        } catch (Throwable t) {
            LogUtil.warn("CRITICAL: Failed to initialize Command Framework. " +
                    "AltarAC will continue to run with no commands.", t);
            return () -> {};
        }
    }

    private CommandManager<Sender> createCloudCommandManager() {
        LegacyPaperCommandManager<Sender> manager = new LegacyPaperCommandManager<>(
                this,
                ExecutionCoordinator.simpleCoordinator(),
                senderFactory.get()
        );
        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            try {
                manager.registerBrigadier();
                CloudBrigadierManager<Sender, ?> cbm = manager.brigadierManager();
                cbm.settings().set(BrigadierSetting.FORCE_EXECUTABLE, true);
            } catch (Throwable t) {
                LogUtil.error("Failed to register Brigadier native completions. Falling back to standard completions.", t);
            }
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }
        return manager;
    }

    public BukkitSenderFactory getBukkitSenderFactory() {
        return senderFactory.get();
    }
}
