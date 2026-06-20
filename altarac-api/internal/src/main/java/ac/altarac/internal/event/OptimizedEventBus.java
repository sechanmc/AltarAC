package ac.altarac.internal.event;

import ac.altarac.api.event.EventBus;
import ac.altarac.api.event.EventChannel;
import ac.altarac.api.event.AltarACEvent;
import ac.altarac.api.event.AltarACEventHandler;
import ac.altarac.api.event.AltarACEventListener;
import ac.altarac.api.event.events.CommandExecuteEvent;
import ac.altarac.api.event.events.CompletePredictionEvent;
import ac.altarac.api.event.events.FlagEvent;
import ac.altarac.api.event.events.AltarACCheckEvent;
import ac.altarac.api.event.events.AltarACJoinEvent;
import ac.altarac.api.event.events.AltarACPlayerSetbackEvent;
import ac.altarac.api.event.events.AltarACQuitEvent;
import ac.altarac.api.event.events.AltarACReloadEvent;
import ac.altarac.api.event.events.AltarACSetbackEvent;
import ac.altarac.api.event.events.AltarACTeleportEvent;
import ac.altarac.api.event.events.AltarACTransactionReceivedEvent;
import ac.altarac.api.event.events.AltarACTransactionSendEvent;
import ac.altarac.api.event.events.AltarACVehicleSetbackEvent;
import ac.altarac.api.event.events.AltarACVerboseCheckEvent;
import ac.altarac.api.plugin.AltarACPlugin;
import ac.altarac.internal.plugin.resolver.AltarACExtensionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Channel-backed event bus implementation.
 *
 * <p>All AltarAC built-in channels are registered explicitly at construction
 * time — no reflection, obfuscation-safe. Addons with their own events can
 * register via {@link #register(Class, EventChannel)}.
 *
 * <p>Legacy APIs ({@link #post(AltarACEvent)}, class-keyed {@code subscribe},
 * reflective {@code registerAnnotatedListeners}) are retained for source
 * compatibility with 1.2.4.0 callers. All three route through the same
 * channel objects as {@link #get(Class)} + the channel's typed
 * {@code on…(…)} methods — a single post therefore reaches typed handlers
 * as well as legacy listeners via each channel's
 * {@link EventChannel#dispatchLegacy(AltarACEvent)}.
 */
public class OptimizedEventBus implements EventBus {
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private final AltarACExtensionManager extensionManager;
    private final ConcurrentMap<Class<?>, EventChannel<?, ?>> channels = new ConcurrentHashMap<>();

    /**
     * Side map of reflective/class-keyed registrations keyed by identity of
     * (pluginContext, listenerInstanceOrClass). Used by
     * {@link #unregisterListeners(Object, Object)} and friends to find the
     * specific entries to remove — Entry itself doesn't carry the originating
     * listener instance. Modifications are guarded by {@code this}.
     */
    private final Map<Object, Map<Object, List<Registration>>> instanceRegistrations = new IdentityHashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public OptimizedEventBus(AltarACExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
        // Built-in channels — direct compile-time references.
        // Renaming any event's nested Channel breaks these lines immediately.

        // Concrete channels.
        FlagEvent.Channel flagCh                           = new FlagEvent.Channel();
        CommandExecuteEvent.Channel commandCh              = new CommandExecuteEvent.Channel();
        CompletePredictionEvent.Channel completeCh         = new CompletePredictionEvent.Channel();
        AltarACJoinEvent.Channel joinCh                       = new AltarACJoinEvent.Channel();
        AltarACQuitEvent.Channel quitCh                       = new AltarACQuitEvent.Channel();
        AltarACReloadEvent.Channel reloadCh                   = new AltarACReloadEvent.Channel();
        AltarACTeleportEvent.Channel teleportCh               = new AltarACTeleportEvent.Channel();
        AltarACTransactionSendEvent.Channel txSendCh          = new AltarACTransactionSendEvent.Channel();
        AltarACTransactionReceivedEvent.Channel txRecvCh      = new AltarACTransactionReceivedEvent.Channel();
        AltarACPlayerSetbackEvent.Channel playerSetbackCh     = new AltarACPlayerSetbackEvent.Channel();
        AltarACVehicleSetbackEvent.Channel vehicleSetbackCh   = new AltarACVehicleSetbackEvent.Channel();
        installChannel(FlagEvent.class,                    flagCh);
        installChannel(CommandExecuteEvent.class,          commandCh);
        installChannel(CompletePredictionEvent.class,      completeCh);
        installChannel(AltarACJoinEvent.class,                joinCh);
        installChannel(AltarACQuitEvent.class,                quitCh);
        installChannel(AltarACReloadEvent.class,              reloadCh);
        installChannel(AltarACTeleportEvent.class,            teleportCh);
        installChannel(AltarACTransactionSendEvent.class,     txSendCh);
        installChannel(AltarACTransactionReceivedEvent.class, txRecvCh);
        installChannel(AltarACPlayerSetbackEvent.class,       playerSetbackCh);
        installChannel(AltarACVehicleSetbackEvent.class,      vehicleSetbackCh);

        // Abstract channels. Their Class<AltarACCheckEvent<?>> keys are raw-cast
        // because AltarACCheckEvent's CHANNEL type parameter is erased at .class.
        AltarACEvent.Channel anyCh                            = new AltarACEvent.Channel();
        AltarACCheckEvent.Channel checkCh                     = new AltarACCheckEvent.Channel();
        AltarACVerboseCheckEvent.Channel verboseCheckCh       = new AltarACVerboseCheckEvent.Channel();
        AltarACSetbackEvent.Channel setbackCh                 = new AltarACSetbackEvent.Channel();
        installAbstractChannel(AltarACEvent.class,             anyCh);
        installAbstractChannel(AltarACCheckEvent.class,        checkCh);
        installAbstractChannel(AltarACVerboseCheckEvent.class, verboseCheckCh);
        installAbstractChannel(AltarACSetbackEvent.class,      setbackCh);

        // Bridge wiring: every concrete subtype registers with every abstract
        // parent it can bridge to. Order matters only when abstract subscribes
        // land before registerSubtype — in that case registerSubtype walks the
        // subscriber list. We wire after installing both channels, so at
        // construction time the subscriber list is empty and the install is
        // just bookkeeping.
        checkCh.registerSubtype(FlagEvent.class,                flagCh,     FlagEvent.Channel::bridgeFromCheck);
        checkCh.registerSubtype(CommandExecuteEvent.class,      commandCh,  CommandExecuteEvent.Channel::bridgeFromCheck);
        checkCh.registerSubtype(CompletePredictionEvent.class,  completeCh, CompletePredictionEvent.Channel::bridgeFromCheck);

        verboseCheckCh.registerSubtype(FlagEvent.class,            flagCh,    FlagEvent.Channel::bridgeFromVerboseCheck);
        verboseCheckCh.registerSubtype(CommandExecuteEvent.class,  commandCh, CommandExecuteEvent.Channel::bridgeFromVerboseCheck);

        setbackCh.registerSubtype(AltarACPlayerSetbackEvent.class,   playerSetbackCh,  AltarACPlayerSetbackEvent.Channel::bridgeFromSetback);
        setbackCh.registerSubtype(AltarACVehicleSetbackEvent.class,  vehicleSetbackCh, AltarACVehicleSetbackEvent.Channel::bridgeFromSetback);

        anyCh.registerSubtype(FlagEvent.class,                    flagCh,           FlagEvent.Channel::bridgeFromAny);
        anyCh.registerSubtype(CommandExecuteEvent.class,          commandCh,        CommandExecuteEvent.Channel::bridgeFromAny);
        anyCh.registerSubtype(CompletePredictionEvent.class,      completeCh,       CompletePredictionEvent.Channel::bridgeFromAny);
        anyCh.registerSubtype(AltarACJoinEvent.class,                joinCh,           AltarACJoinEvent.Channel::bridgeFromAny);
        anyCh.registerSubtype(AltarACQuitEvent.class,                quitCh,           AltarACQuitEvent.Channel::bridgeFromAny);
        anyCh.registerSubtype(AltarACReloadEvent.class,              reloadCh,         AltarACReloadEvent.Channel::bridgeFromAny);
        anyCh.registerSubtype(AltarACTeleportEvent.class,            teleportCh,       AltarACTeleportEvent.Channel::bridgeFromAny);
        anyCh.registerSubtype(AltarACTransactionSendEvent.class,     txSendCh,         AltarACTransactionSendEvent.Channel::bridgeFromAny);
        anyCh.registerSubtype(AltarACTransactionReceivedEvent.class, txRecvCh,         AltarACTransactionReceivedEvent.Channel::bridgeFromAny);
        anyCh.registerSubtype(AltarACPlayerSetbackEvent.class,       playerSetbackCh,  AltarACPlayerSetbackEvent.Channel::bridgeFromAny);
        anyCh.registerSubtype(AltarACVehicleSetbackEvent.class,      vehicleSetbackCh, AltarACVehicleSetbackEvent.Channel::bridgeFromAny);
    }

    private <E extends AltarACEvent<C>, C extends EventChannel<? extends E, ?>> void installChannel(Class<E> eventClass, C channel) {
        channel.setPluginResolver(extensionManager::getPlugin);
        channels.put(eventClass, channel);
    }

    /**
     * Abstract-channel variant of {@link #installChannel}. Abstract events
     * (AltarACEvent, AltarACCheckEvent, AltarACVerboseCheckEvent) are generic on
     * {@code CHANNEL}, which makes the tight
     * {@code E extends AltarACEvent<C>, C extends EventChannel<? extends E, ?>}
     * constraint unsolvable when you pass the raw parent class with a
     * concrete abstract-channel. Loosen the E binding here; the abstract
     * channels are exhaustively listed in the constructor, so the looser
     * type safety is contained.
     */
    private <E, C extends EventChannel<?, ?>> void installAbstractChannel(Class<E> eventClass, C channel) {
        channel.setPluginResolver(extensionManager::getPlugin);
        channels.put(eventClass, channel);
    }

    // ───── Typed channel API ───────────────────────────────────────────────

    @Override
    @SuppressWarnings("unchecked")
    public <E extends AltarACEvent<C>, C extends EventChannel<? extends E, ?>> @NotNull C get(@NotNull Class<E> eventClass) {
        EventChannel<?, ?> ch = channels.get(eventClass);
        if (ch == null) {
            throw new IllegalArgumentException("No EventChannel registered for " + eventClass.getName()
                    + " — addons must call EventBus.register(Class, Channel) before first use.");
        }
        return (C) ch;
    }

    @Override
    public <E extends AltarACEvent<C>, C extends EventChannel<? extends E, ?>> void register(@NotNull Class<E> eventClass, @NotNull C channel) {
        channel.setPluginResolver(extensionManager::getPlugin);
        channels.put(eventClass, channel);
    }

    // ───── Reflective annotated-method registration ────────────────────────

    @Override
    public void registerAnnotatedListeners(@NotNull Object pluginContext, @NotNull Object listener) {
        AltarACPlugin plugin = extensionManager.getPlugin(pluginContext);
        registerAnnotatedListeners(plugin, listener);
    }

    @Override
    public void registerAnnotatedListeners(@NotNull AltarACPlugin plugin, @NotNull Object listener) {
        registerMethods(plugin, listener, listener.getClass(), listener);
    }

    @Override
    public void registerStaticAnnotatedListeners(@NotNull Object pluginContext, @NotNull Class<?> clazz) {
        AltarACPlugin plugin = extensionManager.getPlugin(pluginContext);
        registerStaticAnnotatedListeners(plugin, clazz);
    }

    @Override
    public void registerStaticAnnotatedListeners(@NotNull AltarACPlugin plugin, @NotNull Class<?> clazz) {
        registerMethods(plugin, null, clazz, clazz);
    }

    private void registerMethods(@NotNull AltarACPlugin plugin, @Nullable Object instance, @NotNull Class<?> clazz, @NotNull Object registrationKey) {
        for (Method method : clazz.getDeclaredMethods()) {
            AltarACEventHandler annotation = method.getAnnotation(AltarACEventHandler.class);
            if (annotation == null || method.getParameterCount() != 1) continue;
            Class<?> paramType = method.getParameterTypes()[0];
            if (!AltarACEvent.class.isAssignableFrom(paramType)) continue;
            if (instance == null && !Modifier.isStatic(method.getModifiers())) continue;

            @SuppressWarnings("unchecked")
            Class<? extends AltarACEvent<?>> eventClass = (Class<? extends AltarACEvent<?>>) (Class<?>) paramType;
            EventChannel<?, ?> channel = channels.get(eventClass);
            if (channel == null) continue; // no channel for this event — ignore silently (matches old behavior)

            try {
                method.setAccessible(true);
                MethodHandle handle = lookup.unreflect(method);
                AltarACEventListener<AltarACEvent<?>> listener = buildListener(instance, handle);
                subscribeLegacyInternal(plugin, channel, eventClass, listener,
                        annotation.priority(), annotation.ignoreCancelled(), method.getDeclaringClass(), registrationKey);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /** Wrap a reflected method + instance as a AltarACEventListener. */
    private static AltarACEventListener<AltarACEvent<?>> buildListener(@Nullable Object instance, MethodHandle handle) {
        if (instance != null) {
            return event -> {
                try {
                    handle.invoke(instance, event);
                } catch (Throwable t) {
                    throw new RuntimeException("Failed to invoke listener for " + event.getClass().getName(), t);
                }
            };
        }
        return event -> {
            try {
                handle.invoke(event);
            } catch (Throwable t) {
                throw new RuntimeException("Failed to invoke listener for " + event.getClass().getName(), t);
            }
        };
    }

    // ───── Class-keyed explicit subscribe ──────────────────────────────────

    @Override
    public <T extends AltarACEvent<?>> void subscribe(@NotNull Object pluginContext, @NotNull Class<T> eventType, @NotNull AltarACEventListener<T> listener, int priority, boolean ignoreCancelled, @NotNull Class<?> declaringClass) {
        AltarACPlugin plugin = extensionManager.getPlugin(pluginContext);
        subscribe(plugin, eventType, listener, priority, ignoreCancelled, declaringClass);
    }

    @Override
    public <T extends AltarACEvent<?>> void subscribe(@NotNull AltarACPlugin plugin, @NotNull Class<T> eventType, @NotNull AltarACEventListener<T> listener, int priority, boolean ignoreCancelled, @NotNull Class<?> declaringClass) {
        EventChannel<?, ?> channel = channels.get(eventType);
        if (channel == null) {
            throw new IllegalArgumentException("No EventChannel registered for " + eventType.getName());
        }
        subscribeLegacyInternal(plugin, channel, eventType, listener, priority, ignoreCancelled, declaringClass, listener);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void subscribeLegacyInternal(
            @NotNull AltarACPlugin plugin,
            @NotNull EventChannel<?, ?> channel,
            @NotNull Class<? extends AltarACEvent<?>> eventClass,
            @NotNull AltarACEventListener<?> listener,
            int priority, boolean ignoreCancelled,
            @NotNull Class<?> declaringClass,
            @NotNull Object registrationKey) {
        ((EventChannel) channel).subscribeLegacy(listener, eventClass, priority, ignoreCancelled, plugin, declaringClass);
        synchronized (this) {
            Map<Object, List<Registration>> byPlugin = instanceRegistrations.computeIfAbsent(plugin, p -> new IdentityHashMap<>());
            byPlugin.computeIfAbsent(registrationKey, k -> new ArrayList<>()).add(new Registration(channel, listener));
        }
    }

    // ───── Unregister ──────────────────────────────────────────────────────

    @Override
    public void unregisterListeners(@NotNull Object pluginContext, @NotNull Object listener) {
        unregisterListeners(extensionManager.getPlugin(pluginContext), listener);
    }

    @Override
    public void unregisterListeners(@NotNull AltarACPlugin plugin, @NotNull Object listener) {
        removeInstanceRegistrations(plugin, listener);
    }

    @Override
    public void unregisterStaticListeners(@NotNull Object pluginContext, @NotNull Class<?> clazz) {
        unregisterStaticListeners(extensionManager.getPlugin(pluginContext), clazz);
    }

    @Override
    public void unregisterStaticListeners(@NotNull AltarACPlugin plugin, @NotNull Class<?> clazz) {
        removeInstanceRegistrations(plugin, clazz);
    }

    @Override
    public void unregisterAllListeners(@NotNull Object pluginContext) {
        unregisterAllListeners(extensionManager.getPlugin(pluginContext));
    }

    @Override
    public void unregisterAllListeners(@NotNull AltarACPlugin plugin) {
        List<Registration> all = new ArrayList<>();
        synchronized (this) {
            Map<Object, List<Registration>> byPlugin = instanceRegistrations.remove(plugin);
            if (byPlugin != null) {
                for (List<Registration> regs : byPlugin.values()) all.addAll(regs);
            }
        }
        for (Registration r : all) r.channel.unsubscribeLegacy(r.listener);
        for (EventChannel<?, ?> ch : channels.values()) ch.unsubscribeAllFromPlugin(plugin);
    }

    @Override
    public void unregisterListener(@NotNull Object pluginContext, @NotNull AltarACEventListener<?> listener) {
        unregisterListener(extensionManager.getPlugin(pluginContext), listener);
    }

    @Override
    public void unregisterListener(@NotNull AltarACPlugin plugin, @NotNull AltarACEventListener<?> listener) {
        for (EventChannel<?, ?> ch : channels.values()) ch.unsubscribeLegacy(listener);
        synchronized (this) {
            Map<Object, List<Registration>> byPlugin = instanceRegistrations.get(plugin);
            if (byPlugin != null) {
                for (List<Registration> regs : byPlugin.values()) {
                    regs.removeIf(r -> r.listener == listener);
                }
            }
        }
    }

    private void removeInstanceRegistrations(@NotNull AltarACPlugin plugin, @NotNull Object key) {
        List<Registration> toRemove;
        synchronized (this) {
            Map<Object, List<Registration>> byPlugin = instanceRegistrations.get(plugin);
            if (byPlugin == null) return;
            List<Registration> regs = byPlugin.remove(key);
            if (regs == null) return;
            toRemove = regs;
        }
        for (Registration r : toRemove) r.channel.unsubscribeLegacy(r.listener);
    }

    // ───── Legacy post ─────────────────────────────────────────────────────

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void post(@NotNull AltarACEvent<?> event) {
        // Route to the concrete-class channel. Walks the superclass chain so a
        // hypothetical abstract-event subscriber (via addon-registered channel)
        // still receives the event.
        Class<?> c = event.getClass();
        while (c != null && AltarACEvent.class.isAssignableFrom(c)) {
            EventChannel<?, ?> ch = channels.get(c);
            if (ch != null) {
                ((EventChannel) ch).dispatchLegacy(event);
            }
            c = c.getSuperclass();
        }
    }

    /** Reflective-registration bookkeeping entry. */
    private static final class Registration {
        final EventChannel<?, ?> channel;
        final AltarACEventListener<?> listener;

        Registration(EventChannel<?, ?> channel, AltarACEventListener<?> listener) {
            this.channel = channel;
            this.listener = listener;
        }
    }
}
