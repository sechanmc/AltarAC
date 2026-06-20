package ac.altarac.api.event.events;

import ac.altarac.api.AltarACUser;
import ac.altarac.api.event.EventChannel;
import ac.altarac.api.event.AltarACEvent;
import ac.altarac.api.plugin.AltarACPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class AltarACJoinEvent extends AltarACEvent<AltarACJoinEvent.Channel> implements AltarACUserEvent {
    private AltarACUser user;

    /** Pool constructor — fields populated via {@link #init}. */
    public AltarACJoinEvent() {
        super(true); // Async
    }

    public AltarACJoinEvent(AltarACUser user) {
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
        void onJoin(@NotNull AltarACUser user);
    }

    public static final class Channel extends EventChannel<AltarACJoinEvent, Handler> {
        private final ThreadLocal<AltarACJoinEvent> legacyPool = ThreadLocal.withInitial(AltarACJoinEvent::new);

        public Channel() {
            super(AltarACJoinEvent.class, Handler.class);
        }

        public void onJoin(@NotNull AltarACPlugin plugin, @NotNull Handler handler) {
            subscribe(handler, 0, false, plugin, null);
        }

        public void onJoin(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority) {
            subscribe(handler, priority, false, plugin, null);
        }

        /** @deprecated resolve your context once at plugin enable — {@code api.getPlugin(this)} — and call the {@link AltarACPlugin}-taking overload. */
        @Deprecated
        public void onJoin(@NotNull Object pluginContext, @NotNull Handler handler) {
            onJoin(resolvePlugin(pluginContext), handler);
        }

        /** @deprecated see {@link #onJoin(Object, Handler)}. */
        @Deprecated
        public void onJoin(@NotNull Object pluginContext, @NotNull Handler handler, int priority) {
            onJoin(resolvePlugin(pluginContext), handler, priority);
        }

        public void fire(@NotNull AltarACUser user) {
            Entry<Handler>[] entries = entries();
            if (entries.length == 0) return;
            if (!hasLegacy()) {
                for (Entry<Handler> e : entries) {
                    try {
                        e.handler.onJoin(user);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
                return;
            }
            AltarACJoinEvent pooled = legacyPool.get();
            pooled.init(user);
            for (Entry<Handler> e : entries) {
                try {
                    if (e.legacyListener != null) {
                        e.<AltarACJoinEvent>legacyListenerAs().handle(pooled);
                    } else {
                        e.handler.onJoin(user);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        @Override
        protected boolean dispatchTypedFromLegacy(@NotNull AltarACJoinEvent event, @NotNull Handler handler, boolean cancelled) {
            handler.onJoin(event.getUser());
            return false;
        }

        @ApiStatus.Internal
        public static @NotNull Handler bridgeFromAny(@NotNull ac.altarac.api.event.AltarACEvent.Handler abstractHandler) {
            return user -> abstractHandler.onAnyEvent(AltarACJoinEvent.class, false);
        }
    }
}
