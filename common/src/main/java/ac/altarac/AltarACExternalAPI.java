package ac.altarac;

import ac.altarac.api.AltarACAbstractAPI;
import ac.altarac.api.AltarACUser;
import ac.altarac.api.alerts.AlertManager;
import ac.altarac.api.config.ConfigManager;
import ac.altarac.api.event.EventBus;
import ac.altarac.api.event.events.AltarACReloadEvent;
import ac.altarac.api.plugin.AltarACPlugin;
import ac.altarac.api.storage.backend.BackendRegistry;
import ac.altarac.manager.config.ConfigManagerFileImpl;
import ac.altarac.manager.init.start.StartableInitable;
import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.anticheat.LogUtil;
import ac.altarac.utils.anticheat.MessageUtil;
import ac.altarac.utils.common.ConfigReloadObserver;
import ac.altarac.utils.common.PropertiesUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

// This is used for AltarAC's external API. It has its own class just for organization.
public class AltarACExternalAPI implements AltarACAbstractAPI, ConfigReloadObserver, StartableInitable {

    // Holder class — AltarACExternalAPI is constructed inside AltarACAPI's ctor,
    // so a plain static-final would see a null AltarACAPI.INSTANCE. Holder
    // init runs on first fire, after AltarACAPI is fully built.
    private static final class Channels {
        static final AltarACReloadEvent.Channel RELOAD = AltarACAPI.INSTANCE.getEventBus().get(AltarACReloadEvent.class);
    }

    private final AltarACAPI api;
    @Getter
    private final Map<String, Function<AltarACUser, String>> variableReplacements = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, String> staticReplacements = new ConcurrentHashMap<>();
    private final Map<String, Function<Object, Object>> functions = new ConcurrentHashMap<>();
    private final ConfigManagerFileImpl configManagerFile = new ConfigManagerFileImpl();
    private final String AltarACVersion;
    private ConfigManager configManager = null;
    private boolean started = false;

    public AltarACExternalAPI(AltarACAPI api) {
        this.api = api;
        this.AltarACVersion = resolveVersion(api);
    }

    @Override
    public @NotNull EventBus getEventBus() {
        return api.getEventBus();
    }

    @Override
    public @Nullable AltarACUser getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    @Override
    public @Nullable AltarACUser getUser(UUID uuid) {
        return api.getPlayerDataManager().getPlayer(uuid);
    }

    @Override
    public void registerVariable(String string, Function<AltarACUser, String> replacement) {
        if (replacement == null) {
            variableReplacements.remove(string);
        } else {
            variableReplacements.put(string, replacement);
        }
    }

    @Override
    public void registerVariable(String variable, String replacement) {
        if (replacement == null) {
            staticReplacements.remove(variable);
        } else {
            staticReplacements.put(variable, replacement);
        }
    }

    @Override
    public String getVersion() {
        return AltarACVersion;
    }

    private static String resolveVersion(AltarACAPI api) {
        try {
            Properties properties = PropertiesUtil.readProperties(AltarACExternalAPI.class, "altarac.properties");
            String buildVersion = properties.getProperty("build.version");
            if (buildVersion != null && !buildVersion.isBlank() && !buildVersion.startsWith("${")) {
                return buildVersion;
            }
        } catch (RuntimeException ignored) {
        }

        try {
            return api.getPlugin().getDescription().getVersion();
        } catch (RuntimeException e) {
            return "unknown";
        }
    }

    @Override
    public void registerFunction(String key, Function<Object, Object> function) {
        if (function == null) {
            functions.remove(key);
        } else {
            functions.put(key, function);
        }
    }

    @Override
    public Function<Object, Object> getFunction(String key) {
        return functions.get(key);
    }

    @Override
    public AlertManager getAlertManager() {
        return AltarACAPI.INSTANCE.getAlertManager();
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public int getCurrentTick() {
        return AltarACAPI.INSTANCE.getTickManager().currentTick;
    }

    @Override
    public @NotNull AltarACPlugin getPlugin(@NotNull Object o) {
        return this.api.getExtensionManager().getPlugin(o);
    }

    @Override
    public @NotNull BackendRegistry getBackendRegistry() {
        return api.getBackendRegistry();
    }

    // on load, load the config & register the service
    public void load() {
        reload(configManagerFile);
        api.getLoader().registerAPIService();
    }

    // handles any config loading that's needed to be done after load
    @Override
    public void start() {
        started = true;
        try {
            AltarACAPI.INSTANCE.getConfigManager().start();
        } catch (Exception e) {
            LogUtil.error("Failed to start config manager.", e);
        }
    }

    @Override
    public void reload(ConfigManager config) {
        if (config.isLoadedAsync() && started) {
            AltarACAPI.INSTANCE.getScheduler().getAsyncScheduler().runNow(AltarACAPI.INSTANCE.getPlugin(),
                    () -> successfulReload(config));
        } else {
            successfulReload(config);
        }
    }

    @Override
    public CompletableFuture<Boolean> reloadAsync(ConfigManager config) {
        if (config.isLoadedAsync() && started) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            AltarACAPI.INSTANCE.getScheduler().getAsyncScheduler().runNow(AltarACAPI.INSTANCE.getPlugin(),
                    () -> future.complete(successfulReload(config)));
            return future;
        }
        return CompletableFuture.completedFuture(successfulReload(config));
    }

    private boolean successfulReload(ConfigManager config) {
        try {
            config.reload();
            AltarACAPI.INSTANCE.getConfigManager().load(config);
            if (started) AltarACAPI.INSTANCE.getConfigManager().start();
            onReload(config);
            if (started)
                AltarACAPI.INSTANCE.getScheduler().getAsyncScheduler().runNow(AltarACAPI.INSTANCE.getPlugin(),
                        () -> Channels.RELOAD.fire(true));
            return true;
        } catch (Exception e) {
            LogUtil.error("Failed to reload config", e);
        }
        if (started)
            AltarACAPI.INSTANCE.getScheduler().getAsyncScheduler().runNow(AltarACAPI.INSTANCE.getPlugin(),
                    () -> Channels.RELOAD.fire(false));
        return false;
    }

    @Override
    public void onReload(ConfigManager newConfig) {
        if (newConfig == null) {
            LogUtil.warn("ConfigManager not set. Using default config file manager.");
            configManager = configManagerFile;
        } else {
            configManager = newConfig;
        }
        // Update variables
        updateVariables();
        // Restart
        AltarACAPI.INSTANCE.getAlertManager().reload(configManager);
        AltarACAPI.INSTANCE.getDiscordManager().reload();
        AltarACAPI.INSTANCE.getSpectateManager().reload();
        // First-load guard: load() calls reload() before start() runs, so this fires once with started=false before the datastore exists. Subsequent /AltarAC reload calls see started=true and proceed (including disabled→enabled flips — DataStoreLifecycle.reload() re-evaluates builder.enabled() each time).
        if (!started) return;
        // Hot-reload picks up backend swaps + routing + connection-pool edits without a server restart. Drains in-flight writes for shutdown-drain-timeout-ms then drops; brief mid-reload unavailability is the tradeoff.
        if (AltarACAPI.INSTANCE.getDataStoreLifecycle() != null) {
            AltarACAPI.INSTANCE.getDataStoreLifecycle().reload();
        }
        // Reload checks for all players
        for (AltarACPlayer player : AltarACAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.runSafely(() -> player.reload(configManager));
        }
    }

    private void updateVariables() {
        variableReplacements.putIfAbsent("%player%", AltarACUser::getName);
        variableReplacements.putIfAbsent("%uuid%", user -> user.getUniqueId().toString());
        variableReplacements.putIfAbsent("%ping%", user -> user.getTransactionPing() + "");
        variableReplacements.putIfAbsent("%brand%", AltarACUser::getBrand);
        variableReplacements.putIfAbsent("%h_sensitivity%", user -> ((int) Math.round(user.getHorizontalSensitivity() * 200)) + "");
        variableReplacements.putIfAbsent("%v_sensitivity%", user -> ((int) Math.round(user.getVerticalSensitivity() * 200)) + "");
        variableReplacements.putIfAbsent("%fast_math%", user -> !user.isVanillaMath() + "");
        variableReplacements.putIfAbsent("%tps%", user -> String.format("%.2f", AltarACAPI.INSTANCE.getPlatformServer().getTPS()));
        variableReplacements.putIfAbsent("%version%", AltarACUser::getVersionName);
        // static variables
        staticReplacements.put("%prefix%", MessageUtil.translateAlternateColorCodes('&', AltarACAPI.INSTANCE.getConfigManager().getPrefix()));
        staticReplacements.putIfAbsent("%AltarAC_version%", getVersion());
    }
}
