package ac.altarac.api.event.events;

import ac.altarac.api.AltarACUser;
import ac.altarac.api.event.EventChannel;
import ac.altarac.api.plugin.AltarACPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when AltarAC sends a player-on-foot setback — the
 * {@code ServerPlayerPositionAndLook} / teleport packet branch of
 * {@link AltarACSetbackEvent}.
 *
 * <p>Carries the outbound teleport id so packet-tracking consumers
 * (e.g. sibling anticheats that need to dedupe the teleport-confirm the
 * client will emit in response) can correlate on id. See
 * {@link AltarACTeleportEvent} for the complementary
 * "every outbound teleport packet" signal — that event fires at this
 * site as well so either subscription sees the setback teleport.
 *
 * <p>Fires on the Netty thread associated with the user. Observational,
 * not cancellable.
 */
public final class AltarACPlayerSetbackEvent extends AltarACSetbackEvent<AltarACPlayerSetbackEvent.Channel> {
    private AltarACPlayerSetbackEvent() {
        // Never instantiated — exists only as a Class key for bus.get(AltarACPlayerSetbackEvent.class).
    }

    @FunctionalInterface
    public interface Handler {
        void onPlayerSetback(@NotNull AltarACUser user, int teleportId,
                             double x, double y, double z, long timestamp);
    }

    public static final class Channel extends EventChannel<AltarACPlayerSetbackEvent, Handler> {
        public Channel() {
            super(AltarACPlayerSetbackEvent.class, Handler.class);
        }

        public void onPlayerSetback(@NotNull AltarACPlugin plugin, @NotNull Handler handler) {
            subscribe(handler, 0, false, plugin, null);
        }

        public void onPlayerSetback(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority) {
            subscribe(handler, priority, false, plugin, null);
        }

        /** @deprecated resolve your context once at plugin enable — {@code api.getPlugin(this)} — and call the {@link AltarACPlugin}-taking overload. */
        @Deprecated
        public void onPlayerSetback(@NotNull Object pluginContext, @NotNull Handler handler) {
            onPlayerSetback(resolvePlugin(pluginContext), handler);
        }

        /** @deprecated see {@link #onPlayerSetback(Object, Handler)}. */
        @Deprecated
        public void onPlayerSetback(@NotNull Object pluginContext, @NotNull Handler handler, int priority) {
            onPlayerSetback(resolvePlugin(pluginContext), handler, priority);
        }

        public void fire(@NotNull AltarACUser user, int teleportId,
                         double x, double y, double z, long timestamp) {
            Entry<Handler>[] entries = entries();
            for (Entry<Handler> e : entries) {
                try {
                    e.handler.onPlayerSetback(user, teleportId, x, y, z, timestamp);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        @Override
        protected boolean dispatchTypedFromLegacy(@NotNull AltarACPlayerSetbackEvent event, @NotNull Handler handler, boolean cancelled) {
            // Unreachable — no public constructor, so no caller can post() one.
            throw new UnsupportedOperationException("AltarACPlayerSetbackEvent has no legacy representation");
        }

        /** Bridge from {@link AltarACSetbackEvent.Handler} — used by the abstract channel when a setback-level subscriber registers. */
        @ApiStatus.Internal
        public static @NotNull Handler bridgeFromSetback(@NotNull AltarACSetbackEvent.Handler abstractHandler) {
            return (user, id, x, y, z, ts) -> abstractHandler.onAnySetback(user, ts);
        }

        /** Bridge from root-level {@link ac.altarac.api.event.AltarACEvent.Handler}. */
        @ApiStatus.Internal
        public static @NotNull Handler bridgeFromAny(@NotNull ac.altarac.api.event.AltarACEvent.Handler abstractHandler) {
            return (user, id, x, y, z, ts) -> abstractHandler.onAnyEvent(AltarACPlayerSetbackEvent.class, false);
        }
    }
}
