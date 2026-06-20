package ac.altarac.api.event.events;

import ac.altarac.api.AltarACUser;
import ac.altarac.api.event.EventChannel;
import ac.altarac.api.event.AltarACEvent;
import ac.altarac.api.plugin.AltarACPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when AltarAC sends a teleport packet to the client.
 *
 * <p>Exists to help maintain compatibility with packet-based plugins and other
 * anticheats that track inbound/outbound teleport packets to build a
 * pending-teleport deque.
 *
 * <p>Fires on the Netty thread associated with the PacketEvents user.
 * Observational, not cancellable.
 */
public final class AltarACTeleportEvent extends AltarACEvent<AltarACTeleportEvent.Channel> {
    private AltarACTeleportEvent() {
        // Never instantiated — exists only as a Class key for bus.get(AltarACTeleportEvent.class).
    }

    @FunctionalInterface
    public interface Handler {
        void onTeleport(@NotNull AltarACUser user, int teleportId, long timestamp);
    }

    public static final class Channel extends EventChannel<AltarACTeleportEvent, Handler> {
        public Channel() {
            super(AltarACTeleportEvent.class, Handler.class);
        }

        public void onTeleport(@NotNull AltarACPlugin plugin, @NotNull Handler handler) {
            subscribe(handler, 0, false, plugin, null);
        }

        public void onTeleport(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority) {
            subscribe(handler, priority, false, plugin, null);
        }

        /** @deprecated resolve your context once at plugin enable — {@code api.getPlugin(this)} — and call the {@link AltarACPlugin}-taking overload. */
        @Deprecated
        public void onTeleport(@NotNull Object pluginContext, @NotNull Handler handler) {
            onTeleport(resolvePlugin(pluginContext), handler);
        }

        /** @deprecated see {@link #onTeleport(Object, Handler)}. */
        @Deprecated
        public void onTeleport(@NotNull Object pluginContext, @NotNull Handler handler, int priority) {
            onTeleport(resolvePlugin(pluginContext), handler, priority);
        }

        public void fire(@NotNull AltarACUser user, int teleportId, long timestamp) {
            Entry<Handler>[] entries = entries();
            for (Entry<Handler> e : entries) {
                try {
                    e.handler.onTeleport(user, teleportId, timestamp);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        @Override
        protected boolean dispatchTypedFromLegacy(@NotNull AltarACTeleportEvent event, @NotNull Handler handler, boolean cancelled) {
            // Unreachable — AltarACTeleportEvent has no public constructor, so no caller can post() one.
            throw new UnsupportedOperationException("AltarACTeleportEvent has no legacy representation");
        }

        @org.jetbrains.annotations.ApiStatus.Internal
        public static @NotNull Handler bridgeFromAny(@NotNull ac.altarac.api.event.AltarACEvent.Handler abstractHandler) {
            return (user, id, ts) -> abstractHandler.onAnyEvent(AltarACTeleportEvent.class, false);
        }
    }
}
