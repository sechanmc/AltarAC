package ac.altarac.api.event.events;

import ac.altarac.api.AltarACUser;
import ac.altarac.api.event.EventChannel;
import ac.altarac.api.plugin.AltarACPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when AltarAC sends a player-in-vehicle setback — the
 * {@code ServerVehicleMove} packet branch of {@link AltarACSetbackEvent}.
 *
 * <p>Unlike {@link AltarACPlayerSetbackEvent}, vehicle-move packets carry no
 * teleport id, so there is nothing for a packet-tracking consumer to
 * correlate against an incoming confirm; this event exists primarily for
 * the semantic "AltarAC did a setback" audience (admin tools, stats,
 * anticheat-test harnesses).
 *
 * <p>Fires on the Netty thread associated with the user. Observational,
 * not cancellable.
 */
public final class AltarACVehicleSetbackEvent extends AltarACSetbackEvent<AltarACVehicleSetbackEvent.Channel> {
    private AltarACVehicleSetbackEvent() {
        // Never instantiated — exists only as a Class key for bus.get(AltarACVehicleSetbackEvent.class).
    }

    @FunctionalInterface
    public interface Handler {
        void onVehicleSetback(@NotNull AltarACUser user,
                              double x, double y, double z, long timestamp);
    }

    public static final class Channel extends EventChannel<AltarACVehicleSetbackEvent, Handler> {
        public Channel() {
            super(AltarACVehicleSetbackEvent.class, Handler.class);
        }

        public void onVehicleSetback(@NotNull AltarACPlugin plugin, @NotNull Handler handler) {
            subscribe(handler, 0, false, plugin, null);
        }

        public void onVehicleSetback(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority) {
            subscribe(handler, priority, false, plugin, null);
        }

        /** @deprecated resolve your context once at plugin enable — {@code api.getPlugin(this)} — and call the {@link AltarACPlugin}-taking overload. */
        @Deprecated
        public void onVehicleSetback(@NotNull Object pluginContext, @NotNull Handler handler) {
            onVehicleSetback(resolvePlugin(pluginContext), handler);
        }

        /** @deprecated see {@link #onVehicleSetback(Object, Handler)}. */
        @Deprecated
        public void onVehicleSetback(@NotNull Object pluginContext, @NotNull Handler handler, int priority) {
            onVehicleSetback(resolvePlugin(pluginContext), handler, priority);
        }

        public void fire(@NotNull AltarACUser user,
                         double x, double y, double z, long timestamp) {
            Entry<Handler>[] entries = entries();
            for (Entry<Handler> e : entries) {
                try {
                    e.handler.onVehicleSetback(user, x, y, z, timestamp);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        @Override
        protected boolean dispatchTypedFromLegacy(@NotNull AltarACVehicleSetbackEvent event, @NotNull Handler handler, boolean cancelled) {
            // Unreachable — no public constructor, so no caller can post() one.
            throw new UnsupportedOperationException("AltarACVehicleSetbackEvent has no legacy representation");
        }

        /** Bridge from {@link AltarACSetbackEvent.Handler} — used by the abstract channel when a setback-level subscriber registers. */
        @ApiStatus.Internal
        public static @NotNull Handler bridgeFromSetback(@NotNull AltarACSetbackEvent.Handler abstractHandler) {
            return (user, x, y, z, ts) -> abstractHandler.onAnySetback(user, ts);
        }

        /** Bridge from root-level {@link ac.altarac.api.event.AltarACEvent.Handler}. */
        @ApiStatus.Internal
        public static @NotNull Handler bridgeFromAny(@NotNull ac.altarac.api.event.AltarACEvent.Handler abstractHandler) {
            return (user, x, y, z, ts) -> abstractHandler.onAnyEvent(AltarACVehicleSetbackEvent.class, false);
        }
    }
}
