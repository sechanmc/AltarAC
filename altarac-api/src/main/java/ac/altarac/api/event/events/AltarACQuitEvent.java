package ac.altarac.api.event.events;

import ac.altarac.api.AltarACUser;
import ac.altarac.api.event.EventChannel;
import ac.altarac.api.event.AltarACEvent;
import ac.altarac.api.plugin.AltarACPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class AltarACQuitEvent extends AltarACEvent<AltarACQuitEvent.Channel> implements AltarACUserEvent {
    private AltarACUser user;

    /** Pool constructor — fields populated via {@link #init}. */
    public AltarACQuitEvent() {
        super(true); // Async
    }

    public AltarACQuitEvent(AltarACUser user) {
        super(true); // Async
        this.user = user;
    }

    @ApiStatus.Internal
    public void init(AltarACUser user) {
        resetForReuse();
        this.user = user;
    }

    @Override
    public AltarACUser getUser() {
        return user;
    }

    @FunctionalInterface
    public interface Handler {
        void onQuit(@NotNull AltarACUser user);
    }

    public static final class Channel extends EventChannel<AltarACQuitEvent, Handler> {
        private final ThreadLocal<AltarACQuitEvent> legacyPool = ThreadLocal.withInitial(AltarACQuitEvent::new);

        public Channel() {
            super(AltarACQuitEvent.class, Handler.class);
        }

        public void onQuit(@NotNull AltarACPlugin plugin, @NotNull Handler handler) {
            subscribe(handler, 0, false, plugin, null);
        }

        public void onQuit(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority) {
            subscribe(handler, priority, false, plugin, null);
        }

        /** @deprecated resolve your context once at plugin enable — {@code api.getPlugin(this)} — and call the {@link AltarACPlugin}-taking overload. */
        @Deprecated
        public void onQuit(@NotNull Object pluginContext, @NotNull Handler handler) {
            onQuit(resolvePlugin(pluginContext), handler);
        }

        /** @deprecated see {@link #onQuit(Object, Handler)}. */
        @Deprecated
        public void onQuit(@NotNull Object pluginContext, @NotNull Handler handler, int priority) {
            onQuit(resolvePlugin(pluginContext), handler, priority);
        }

        public void fire(@NotNull AltarACUser user) {
            Entry<Handler>[] entries = entries();
            if (entries.length == 0) return;
            if (!hasLegacy()) {
                for (Entry<Handler> e : entries) {
                    try {
                        e.handler.onQuit(user);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
                return;
            }
            AltarACQuitEvent pooled = legacyPool.get();
            pooled.init(user);
            for (Entry<Handler> e : entries) {
                try {
                    if (e.legacyListener != null) {
                        e.<AltarACQuitEvent>legacyListenerAs().handle(pooled);
                    } else {
                        e.handler.onQuit(user);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        @Override
        protected boolean dispatchTypedFromLegacy(@NotNull AltarACQuitEvent event, @NotNull Handler handler, boolean cancelled) {
            handler.onQuit(event.getUser());
            return false;
        }

        @ApiStatus.Internal
        public static @NotNull Handler bridgeFromAny(@NotNull ac.altarac.api.event.AltarACEvent.Handler abstractHandler) {
            return user -> abstractHandler.onAnyEvent(AltarACQuitEvent.class, false);
        }
    }
}
