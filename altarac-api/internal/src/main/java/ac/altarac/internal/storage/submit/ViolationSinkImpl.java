package ac.altarac.internal.storage.submit;

import ac.altarac.api.storage.DataStore;
import ac.altarac.api.storage.category.Categories;
import ac.altarac.api.storage.event.ViolationEvent;
import ac.altarac.api.storage.submit.SubmitResult;
import ac.altarac.api.storage.submit.ViolationSink;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Non-blocking submit into the DataStore's VIOLATION ring. Ring accounting (drop,
 * overflow, error) lives on the {@link ac.altarac.internal.storage.core.RingRegistry};
 * this class just forwards.
 */
@ApiStatus.Internal
public final class ViolationSinkImpl implements ViolationSink {

    private final DataStore store;
    private volatile boolean closed;

    public ViolationSinkImpl(DataStore store) {
        this.store = store;
    }

    @Override
    public @NotNull SubmitResult record(@NotNull Consumer<ViolationEvent> configurer) {
        if (closed) return SubmitResult.DROPPED_SHUTTING_DOWN;
        store.submit(Categories.VIOLATION, configurer);
        // submit() is non-blocking; overflow is reported through metrics.
        return SubmitResult.QUEUED;
    }

    public void shutDown() {
        closed = true;
    }
}
