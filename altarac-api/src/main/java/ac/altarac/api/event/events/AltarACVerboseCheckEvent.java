package ac.altarac.api.event.events;

import ac.altarac.api.AbstractCheck;
import ac.altarac.api.AltarACUser;
import ac.altarac.api.event.AbstractEventChannel;
import ac.altarac.api.event.EventChannel;
import ac.altarac.api.plugin.AltarACPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public abstract class AltarACVerboseCheckEvent<CHANNEL extends EventChannel<?, ?>>
        extends AltarACCheckEvent<CHANNEL> {
    private Supplier<String> verboseSupplier = () -> "";

    /** Pool constructor — fields populated via {@link #init(AltarACUser, AbstractCheck, String)}. */
    protected AltarACVerboseCheckEvent() {
        super();
    }

    public AltarACVerboseCheckEvent(AltarACUser user, AbstractCheck check, String verbose) {
        super(user, check);
        setVerboseSupplier(VerboseSuppliers.constant(verbose));
    }

    public AltarACVerboseCheckEvent(AltarACUser user, AbstractCheck check, Supplier<String> verboseSupplier) {
        super(user, check);
        setVerboseSupplier(verboseSupplier);
    }

    @ApiStatus.Internal
    protected void init(AltarACUser user, AbstractCheck check, String verbose) {
        init(user, check, VerboseSuppliers.constant(verbose));
    }

    @ApiStatus.Internal
    protected void init(AltarACUser user, AbstractCheck check, Supplier<String> verboseSupplier) {
        super.init(user, check);
        setVerboseSupplier(verboseSupplier);
    }

    /** Returns the human verbose string, rendering it lazily on first use. */
    public @NotNull String getVerbose() {
        return verboseSupplier.get();
    }

    @ApiStatus.Internal
    final @NotNull Supplier<String> verboseSupplier() {
        return verboseSupplier;
    }

    private void setVerboseSupplier(Supplier<String> verboseSupplier) {
        this.verboseSupplier = VerboseSuppliers.memoize(verboseSupplier);
    }

    /**
     * Abstract-level verbose-check handler. Fires for every concrete
     * {@code AltarACVerboseCheckEvent} subtype — FlagEvent and
     * CommandExecuteEvent out of the box, plus any addon subtypes that
     * opt into bridging. Does not fire for
     * {@link CompletePredictionEvent}, which extends {@link AltarACCheckEvent}
     * directly and has no verbose field.
     */
    @FunctionalInterface
    public interface Handler {
        boolean onVerboseCheck(@NotNull AltarACUser user, @NotNull AbstractCheck check,
                               @NotNull String verbose, boolean currentlyCancelled);
    }

    @FunctionalInterface
    public interface SupplierHandler {
        boolean onVerboseCheck(@NotNull AltarACUser user, @NotNull AbstractCheck check,
                               @NotNull Supplier<String> verbose, boolean currentlyCancelled);
    }

    public static final class Channel extends AbstractEventChannel<AltarACVerboseCheckEvent<?>, SupplierHandler> {
        private static final AtomicBoolean STRING_HANDLER_WARNING = new AtomicBoolean();

        @SuppressWarnings({"unchecked", "rawtypes"})
        public Channel() {
            super((Class<AltarACVerboseCheckEvent<?>>) (Class) AltarACVerboseCheckEvent.class, SupplierHandler.class);
        }

        public void onVerboseCheckSupplier(@NotNull AltarACPlugin plugin, @NotNull SupplierHandler handler) {
            subscribeAbstract(handler, 0, false, plugin);
        }

        public void onVerboseCheckSupplier(@NotNull AltarACPlugin plugin, @NotNull SupplierHandler handler, int priority) {
            subscribeAbstract(handler, priority, false, plugin);
        }

        public void onVerboseCheckSupplier(@NotNull AltarACPlugin plugin, @NotNull SupplierHandler handler, int priority, boolean ignoreCancelled) {
            subscribeAbstract(handler, priority, ignoreCancelled, plugin);
        }

        /**
         * @deprecated Prefer {@link #onVerboseCheckSupplier(AltarACPlugin, SupplierHandler)}.
         */
        @Deprecated
        public void onVerboseCheck(@NotNull AltarACPlugin plugin, @NotNull Handler handler) {
            warnStringHandler(plugin);
            onVerboseCheckSupplier(plugin, adapt(handler));
        }

        /**
         * @deprecated Prefer {@link #onVerboseCheckSupplier(AltarACPlugin, SupplierHandler, int)}.
         */
        @Deprecated
        public void onVerboseCheck(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority) {
            warnStringHandler(plugin);
            onVerboseCheckSupplier(plugin, adapt(handler), priority);
        }

        /**
         * @deprecated Prefer {@link #onVerboseCheckSupplier(AltarACPlugin, SupplierHandler, int, boolean)}.
         */
        @Deprecated
        public void onVerboseCheck(@NotNull AltarACPlugin plugin, @NotNull Handler handler, int priority, boolean ignoreCancelled) {
            warnStringHandler(plugin);
            onVerboseCheckSupplier(plugin, adapt(handler), priority, ignoreCancelled);
        }

        /** @deprecated resolve your context once at plugin enable — {@code api.getPlugin(this)} — and call the {@link AltarACPlugin}-taking overload. */
        @Deprecated
        public void onVerboseCheckSupplier(@NotNull Object pluginContext, @NotNull SupplierHandler handler) {
            subscribeAbstractResolving(pluginContext, handler, 0, false);
        }

        /** @deprecated see {@link #onVerboseCheckSupplier(Object, SupplierHandler)}. */
        @Deprecated
        public void onVerboseCheckSupplier(@NotNull Object pluginContext, @NotNull SupplierHandler handler, int priority) {
            subscribeAbstractResolving(pluginContext, handler, priority, false);
        }

        /** @deprecated see {@link #onVerboseCheckSupplier(Object, SupplierHandler)}. */
        @Deprecated
        public void onVerboseCheckSupplier(@NotNull Object pluginContext, @NotNull SupplierHandler handler, int priority, boolean ignoreCancelled) {
            subscribeAbstractResolving(pluginContext, handler, priority, ignoreCancelled);
        }

        /** @deprecated resolve your context once at plugin enable — {@code api.getPlugin(this)} — and call the {@link AltarACPlugin}-taking overload. */
        @Deprecated
        public void onVerboseCheck(@NotNull Object pluginContext, @NotNull Handler handler) {
            AltarACPlugin plugin = resolvePlugin(pluginContext);
            onVerboseCheck(plugin, handler);
        }

        /** @deprecated see {@link #onVerboseCheck(Object, Handler)}. */
        @Deprecated
        public void onVerboseCheck(@NotNull Object pluginContext, @NotNull Handler handler, int priority) {
            AltarACPlugin plugin = resolvePlugin(pluginContext);
            onVerboseCheck(plugin, handler, priority);
        }

        /** @deprecated see {@link #onVerboseCheck(Object, Handler)}. */
        @Deprecated
        public void onVerboseCheck(@NotNull Object pluginContext, @NotNull Handler handler, int priority, boolean ignoreCancelled) {
            AltarACPlugin plugin = resolvePlugin(pluginContext);
            onVerboseCheck(plugin, handler, priority, ignoreCancelled);
        }

        private static @NotNull SupplierHandler adapt(@NotNull Handler handler) {
            return (user, check, verbose, cancelled) -> handler.onVerboseCheck(user, check, verbose.get(), cancelled);
        }

        private static void warnStringHandler(@NotNull AltarACPlugin plugin) {
            if (STRING_HANDLER_WARNING.compareAndSet(false, true)) {
                plugin.getLogger().warning("Deprecated AltarAC verbose string listener registered; use the Supplier<String> verbose handler and call verbose.get() only when text is needed.");
            }
        }
    }
}
