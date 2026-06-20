package ac.altarac.api.event.events;

import ac.altarac.api.AltarACUser;
import ac.altarac.api.event.EventChannel;
import ac.altarac.api.event.AltarACEvent;
import ac.altarac.api.plugin.AltarACPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when AltarAC sends a transaction packet to the client.
 *
 * <p>Plugins can use this event to track transaction ids issued by AltarAC and
 * correlate them with the matching {@link AltarACTransactionReceivedEvent} once a
 * response is received.
 *
 * <p>Fires on the Netty thread associated with the user. Observational, not
 * cancellable.
 */
public final class AltarACTransactionSendEvent extends AltarACEvent<AltarACTransactionSendEvent.Channel> {
    private AltarACTransactionSendEvent() {
        // Never instantiated — exists only as a Class key for bus.get(AltarACTransactionSendEvent.class).
    }

    @FunctionalInterface
    public interface Handler {
        void onTransactionSend(@NotNull AltarACUser user, int transactionId, long timestamp);
    }

    public static final class Channel extends EventChannel<AltarACTransactionSendEvent, Handler> {
        public Channel() {
            super(AltarACTransactionSendEvent.class, Handler.class);
        }

        public void onTransactionSend(@NotNull AltarACPlugin plugin, @NotNull Handler handler) {
            subscribe(handler, 0, false, plugin, null);
        }

        public void onTransactionSend(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority) {
            subscribe(handler, priority, false, plugin, null);
        }

        /** @deprecated resolve your context once at plugin enable — {@code api.getPlugin(this)} — and call the {@link AltarACPlugin}-taking overload. */
        @Deprecated
        public void onTransactionSend(@NotNull Object pluginContext, @NotNull Handler handler) {
            onTransactionSend(resolvePlugin(pluginContext), handler);
        }

        /** @deprecated see {@link #onTransactionSend(Object, Handler)}. */
        @Deprecated
        public void onTransactionSend(@NotNull Object pluginContext, @NotNull Handler handler, int priority) {
            onTransactionSend(resolvePlugin(pluginContext), handler, priority);
        }

        public void fire(@NotNull AltarACUser user, int transactionId, long timestamp) {
            Entry<Handler>[] entries = entries();
            for (Entry<Handler> e : entries) {
                try {
                    e.handler.onTransactionSend(user, transactionId, timestamp);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        @Override
        protected boolean dispatchTypedFromLegacy(@NotNull AltarACTransactionSendEvent event, @NotNull Handler handler, boolean cancelled) {
            throw new UnsupportedOperationException("AltarACTransactionSendEvent has no legacy representation");
        }

        @org.jetbrains.annotations.ApiStatus.Internal
        public static @NotNull Handler bridgeFromAny(@NotNull ac.altarac.api.event.AltarACEvent.Handler abstractHandler) {
            return (user, id, ts) -> abstractHandler.onAnyEvent(AltarACTransactionSendEvent.class, false);
        }
    }
}
