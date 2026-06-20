package ac.altarac.api.event.events;

import ac.altarac.api.AltarACUser;
import ac.altarac.api.event.EventChannel;
import ac.altarac.api.event.AltarACEvent;
import ac.altarac.api.plugin.AltarACPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when AltarAC receives an inbound response for a transaction packet that
 * it previously sent.
 *
 * <p>AltarAC cancels these inbound packets by default, controlled by the
 * {@code disable-pong-cancelling} option in {@code config.yml}. The
 * {@code packetCancelled} parameter reflects whether AltarAC cancelled packet
 * handling; it is not an event-cancellation flag (this event is observational
 * and not cancellable).
 *
 * <p>Only fires for transactions initiated by AltarAC, on the Netty thread
 * associated with the user.
 */
public final class AltarACTransactionReceivedEvent extends AltarACEvent<AltarACTransactionReceivedEvent.Channel> {
    private AltarACTransactionReceivedEvent() {
        // Never instantiated — exists only as a Class key for bus.get(AltarACTransactionReceivedEvent.class).
    }

    @FunctionalInterface
    public interface Handler {
        void onTransactionReceived(@NotNull AltarACUser user, int transactionId, boolean packetCancelled, long timestamp);
    }

    public static final class Channel extends EventChannel<AltarACTransactionReceivedEvent, Handler> {
        public Channel() {
            super(AltarACTransactionReceivedEvent.class, Handler.class);
        }

        public void onTransactionReceived(@NotNull AltarACPlugin plugin, @NotNull Handler handler) {
            subscribe(handler, 0, false, plugin, null);
        }

        public void onTransactionReceived(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority) {
            subscribe(handler, priority, false, plugin, null);
        }

        /** @deprecated resolve your context once at plugin enable — {@code api.getPlugin(this)} — and call the {@link AltarACPlugin}-taking overload. */
        @Deprecated
        public void onTransactionReceived(@NotNull Object pluginContext, @NotNull Handler handler) {
            onTransactionReceived(resolvePlugin(pluginContext), handler);
        }

        /** @deprecated see {@link #onTransactionReceived(Object, Handler)}. */
        @Deprecated
        public void onTransactionReceived(@NotNull Object pluginContext, @NotNull Handler handler, int priority) {
            onTransactionReceived(resolvePlugin(pluginContext), handler, priority);
        }

        public void fire(@NotNull AltarACUser user, int transactionId, boolean packetCancelled, long timestamp) {
            Entry<Handler>[] entries = entries();
            for (Entry<Handler> e : entries) {
                try {
                    e.handler.onTransactionReceived(user, transactionId, packetCancelled, timestamp);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        @Override
        protected boolean dispatchTypedFromLegacy(@NotNull AltarACTransactionReceivedEvent event, @NotNull Handler handler, boolean cancelled) {
            throw new UnsupportedOperationException("AltarACTransactionReceivedEvent has no legacy representation");
        }

        @org.jetbrains.annotations.ApiStatus.Internal
        public static @NotNull Handler bridgeFromAny(@NotNull ac.altarac.api.event.AltarACEvent.Handler abstractHandler) {
            return (user, id, c, ts) -> abstractHandler.onAnyEvent(AltarACTransactionReceivedEvent.class, false);
        }
    }
}
