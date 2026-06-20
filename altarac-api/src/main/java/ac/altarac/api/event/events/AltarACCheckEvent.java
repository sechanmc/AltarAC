package ac.altarac.api.event.events;

import ac.altarac.api.AbstractCheck;
import ac.altarac.api.AltarACUser;
import ac.altarac.api.event.AbstractEventChannel;
import ac.altarac.api.event.Cancellable;
import ac.altarac.api.event.EventChannel;
import ac.altarac.api.event.AltarACEvent;
import ac.altarac.api.plugin.AltarACPlugin;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class AltarACCheckEvent<CHANNEL extends EventChannel<?, ?>>
        extends AltarACEvent<CHANNEL> implements AltarACUserEvent, Cancellable {
    private AltarACUser user;
    @Getter
    protected AbstractCheck check;
    private boolean cancelled;

    /** Pool constructor — fields populated via {@link #init(AltarACUser, AbstractCheck)}. */
    protected AltarACCheckEvent() {
        super(true); // Async
    }

    public AltarACCheckEvent(AltarACUser user, AbstractCheck check) {
        super(true); // Async
        this.user = user;
        this.check = check;
    }

    @ApiStatus.Internal
    protected void init(AltarACUser user, AbstractCheck check) {
        resetForReuse();
        this.user = user;
        this.check = check;
        this.cancelled = false;
    }

    @Override
    public AltarACUser getUser() {
        return user;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    public double getViolations() {
        return check.getViolations();
    }

    public boolean isSetback() {
        return check.getViolations() > check.getSetbackVL();
    }

    /**
     * Abstract-level check handler. Fires for every concrete
     * {@code AltarACCheckEvent} subtype (FlagEvent, CompletePredictionEvent,
     * CommandExecuteEvent, and any addon subtypes that opt into bridging).
     *
     * <p>Returns the new cancelled state — the value is threaded back into
     * the priority-ordered dispatch loop of whichever concrete subtype
     * fired, so a high-priority abstract subscriber can cancel and
     * lower-priority direct subscribers to the concrete event see the
     * cancellation just like any other priority-ordered handler.
     */
    @FunctionalInterface
    public interface Handler {
        boolean onCheck(@NotNull AltarACUser user, @NotNull AbstractCheck check, boolean currentlyCancelled);
    }

    public static final class Channel extends AbstractEventChannel<AltarACCheckEvent<?>, Handler> {
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Channel() {
            super((Class<AltarACCheckEvent<?>>) (Class) AltarACCheckEvent.class, Handler.class);
        }

        public void onCheck(@NotNull AltarACPlugin plugin, @NotNull Handler handler) {
            subscribeAbstract(handler, 0, false, plugin);
        }

        public void onCheck(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority) {
            subscribeAbstract(handler, priority, false, plugin);
        }

        public void onCheck(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority, boolean ignoreCancelled) {
            subscribeAbstract(handler, priority, ignoreCancelled, plugin);
        }

        /** @deprecated resolve your context once at plugin enable — {@code api.getPlugin(this)} — and call the {@link AltarACPlugin}-taking overload. */
        @Deprecated
        public void onCheck(@NotNull Object pluginContext, @NotNull Handler handler) {
            subscribeAbstractResolving(pluginContext, handler, 0, false);
        }

        /** @deprecated see {@link #onCheck(Object, Handler)}. */
        @Deprecated
        public void onCheck(@NotNull Object pluginContext, @NotNull Handler handler, int priority) {
            subscribeAbstractResolving(pluginContext, handler, priority, false);
        }

        /** @deprecated see {@link #onCheck(Object, Handler)}. */
        @Deprecated
        public void onCheck(@NotNull Object pluginContext, @NotNull Handler handler, int priority, boolean ignoreCancelled) {
            subscribeAbstractResolving(pluginContext, handler, priority, ignoreCancelled);
        }
    }
}
