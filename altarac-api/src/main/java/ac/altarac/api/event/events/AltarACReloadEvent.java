package ac.altarac.api.event.events;

import ac.altarac.api.event.EventChannel;
import ac.altarac.api.event.AltarACEvent;
import ac.altarac.api.plugin.AltarACPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class AltarACReloadEvent extends AltarACEvent<AltarACReloadEvent.Channel> {
    private boolean success;

    /** Pool constructor — fields populated via {@link #init}. */
    public AltarACReloadEvent() {
        super(true); // Async
    }

    public AltarACReloadEvent(boolean success) {
        super(true); // Async
        this.success = success;
    }

    @ApiStatus.Internal
    public void init(boolean success) {
        resetForReuse();
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    @FunctionalInterface
    public interface Handler {
        void onReload(boolean success);
    }

    public static final class Channel extends EventChannel<AltarACReloadEvent, Handler> {
        private final ThreadLocal<AltarACReloadEvent> legacyPool = ThreadLocal.withInitial(AltarACReloadEvent::new);

        public Channel() {
            super(AltarACReloadEvent.class, Handler.class);
        }

        public void onReload(@NotNull AltarACPlugin plugin, @NotNull Handler handler) {
            subscribe(handler, 0, false, plugin, null);
        }

        public void onReload(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority) {
            subscribe(handler, priority, false, plugin, null);
        }

        /** @deprecated resolve your context once at plugin enable — {@code api.getPlugin(this)} — and call the {@link AltarACPlugin}-taking overload. */
        @Deprecated
        public void onReload(@NotNull Object pluginContext, @NotNull Handler handler) {
            onReload(resolvePlugin(pluginContext), handler);
        }

        /** @deprecated see {@link #onReload(Object, Handler)}. */
        @Deprecated
        public void onReload(@NotNull Object pluginContext, @NotNull Handler handler, int priority) {
            onReload(resolvePlugin(pluginContext), handler, priority);
        }

        public void fire(boolean success) {
            Entry<Handler>[] entries = entries();
            if (entries.length == 0) return;
            if (!hasLegacy()) {
                for (Entry<Handler> e : entries) {
                    try {
                        e.handler.onReload(success);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
                return;
            }
            AltarACReloadEvent pooled = legacyPool.get();
            pooled.init(success);
            for (Entry<Handler> e : entries) {
                try {
                    if (e.legacyListener != null) {
                        e.<AltarACReloadEvent>legacyListenerAs().handle(pooled);
                    } else {
                        e.handler.onReload(success);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        @Override
        protected boolean dispatchTypedFromLegacy(@NotNull AltarACReloadEvent event, @NotNull Handler handler, boolean cancelled) {
            handler.onReload(event.isSuccess());
            return false;
        }

        @ApiStatus.Internal
        public static @NotNull Handler bridgeFromAny(@NotNull ac.altarac.api.event.AltarACEvent.Handler abstractHandler) {
            return success -> abstractHandler.onAnyEvent(AltarACReloadEvent.class, false);
        }
    }
}
