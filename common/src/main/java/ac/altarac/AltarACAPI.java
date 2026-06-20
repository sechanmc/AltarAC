package ac.altarac;

import ac.altarac.api.event.EventBus;
import ac.altarac.api.plugin.AltarACPlugin;
import ac.altarac.api.storage.backend.BackendRegistry;
import ac.altarac.internal.plugin.resolver.AltarACExtensionManager;
import ac.altarac.internal.event.OptimizedEventBus;
import ac.altarac.internal.storage.backend.BackendRegistryImpl;
import ac.altarac.internal.storage.backend.memory.InMemoryBackendProvider;
import ac.altarac.internal.storage.backend.mongo.MongoBackendProvider;
import ac.altarac.internal.storage.backend.mysql.MysqlBackendProvider;
import ac.altarac.internal.storage.backend.postgres.PostgresBackendProvider;
import ac.altarac.internal.storage.backend.redis.RedisBackendProvider;
import ac.altarac.internal.storage.backend.sqlite.SqliteBackendProvider;
import ac.altarac.manager.AlertManagerImpl;
import ac.altarac.manager.DiscordManager;
import ac.altarac.manager.InitManager;
import ac.altarac.manager.SpectateManager;
import ac.altarac.manager.TickManager;
import ac.altarac.manager.config.BaseConfigManager;
import ac.altarac.manager.datastore.DataStoreLifecycle;
import ac.altarac.manager.init.Initable;
import ac.altarac.platform.api.Platform;
import ac.altarac.platform.api.PlatformLoader;
import ac.altarac.platform.api.PlatformServer;
import ac.altarac.platform.api.command.CommandService;
import ac.altarac.platform.api.manager.ItemResetHandler;
import ac.altarac.platform.api.manager.MessagePlaceHolderManager;
import ac.altarac.platform.api.manager.PermissionRegistrationManager;
import ac.altarac.platform.api.manager.PlatformPluginManager;
import ac.altarac.platform.api.player.PlatformPlayerFactory;
import ac.altarac.platform.api.scheduler.PlatformScheduler;
import ac.altarac.platform.api.sender.SenderFactory;
import ac.altarac.utils.anticheat.PlayerDataManager;
import ac.altarac.utils.common.arguments.CommonAltarACArguments;
import ac.altarac.utils.reflection.ReflectionUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public final class AltarACAPI {
    public static final AltarACAPI INSTANCE = new AltarACAPI();

    private final Platform platform = detectPlatform();
    private final BaseConfigManager configManager;
    private final AlertManagerImpl alertManager;
    private final SpectateManager spectateManager;
    private final DiscordManager discordManager;
    private final PlayerDataManager playerDataManager;
    private final TickManager tickManager;
    private final AltarACExtensionManager extensionManager;
    private final EventBus eventBus;
    private final AltarACExternalAPI externalAPI;
    private DataStoreLifecycle dataStoreLifecycle;
    private final BackendRegistry backendRegistry = buildBackendRegistry();
    private PlatformLoader loader;
    private InitManager initManager;
    private boolean initialized = false;

    private AltarACAPI() {
        this.configManager = new BaseConfigManager();
        this.alertManager = new AlertManagerImpl();
        this.spectateManager = new SpectateManager();
        this.discordManager = new DiscordManager();
        this.playerDataManager = new PlayerDataManager();
        this.tickManager = new TickManager();
        this.extensionManager = new AltarACExtensionManager();
        this.eventBus = new OptimizedEventBus(extensionManager);
        this.externalAPI = new AltarACExternalAPI(this);
    }

    // the order matters
    private static Platform detectPlatform() {
        Platform override = CommonAltarACArguments.PLATFORM_OVERRIDE.value();
        if (override != null) return override;
        if (ReflectionUtils.hasClass("io.papermc.paper.threadedregions.RegionizedServer")) return Platform.FOLIA;
        if (ReflectionUtils.hasClass("org.bukkit.Bukkit")) return Platform.BUKKIT;
        if (ReflectionUtils.hasClass("net.fabricmc.loader.api.FabricLoader")) return Platform.FABRIC;
        throw new IllegalStateException("Unknown platform!");
    }

    public void load(PlatformLoader platformLoader, Initable... platformSpecificInitables) {
        this.loader = platformLoader;
        this.dataStoreLifecycle = new DataStoreLifecycle(getPlugin(), backendRegistry);
        this.initManager = new InitManager(loader.getPacketEvents(), platformSpecificInitables);
        this.initManager.load();
        this.initialized = true;
    }

    private static BackendRegistry buildBackendRegistry() {
        BackendRegistryImpl registry = new BackendRegistryImpl();
        registry.register(new SqliteBackendProvider());
        registry.register(new InMemoryBackendProvider());
        registry.register(new MysqlBackendProvider());
        registry.register(new PostgresBackendProvider());
        registry.register(new MongoBackendProvider());
        registry.register(new RedisBackendProvider());
        return registry;
    }

    public void start() {
        checkInitialized();
        initManager.start();
    }

    public void stop() {
        checkInitialized();
        initManager.stop();
    }

    public PlatformScheduler getScheduler() {
        return loader.getScheduler();
    }

    public PlatformPlayerFactory getPlatformPlayerFactory() {
        return loader.getPlatformPlayerFactory();
    }

    public AltarACPlugin getPlugin() {
        return loader.getPlugin();
    }

    public SenderFactory<?> getSenderFactory() {
        return loader.getSenderFactory();
    }

    public ItemResetHandler getItemResetHandler() {
        return loader.getItemResetHandler();
    }

    public PlatformPluginManager getPluginManager() {
        return loader.getPluginManager();
    }

    public PlatformServer getPlatformServer() {
        return loader.getPlatformServer();
    }

    public @NotNull MessagePlaceHolderManager getMessagePlaceHolderManager() {
        return loader.getMessagePlaceHolderManager();
    }

    public CommandService getCommandService() {
        return loader.getCommandService();
    }

    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("AltarACAPI has not been initialized!");
        }
    }

    public PermissionRegistrationManager getPermissionManager() {
        return loader.getPermissionManager();
    }
}
