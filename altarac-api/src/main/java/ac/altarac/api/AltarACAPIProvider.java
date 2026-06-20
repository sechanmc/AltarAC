package ac.altarac.api;

import java.util.concurrent.CompletableFuture;

public final class AltarACAPIProvider {
    private static AltarACAbstractAPI instance;
    private static final CompletableFuture<AltarACAbstractAPI> futureInstance = new CompletableFuture<>();

    private AltarACAPIProvider() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initializes the AltarACAPI instance during mod loading.
     * This method should only be called once by the mod initializer.
     *
     * @param api The AltarACAbstractAPI instance to initialize.
     * @throws IllegalStateException If the API is already initialized.
     */
    public static void init(AltarACAbstractAPI api) {
        if (instance != null || futureInstance.isDone()) {
            throw new IllegalStateException("AltarACAPI is already initialized");
        }
        instance = api;
        futureInstance.complete(api); // Complete the future with the API instance
    }

    /**
     * Gets the AltarACAPI instance synchronously.
     *
     * @return The AltarACAbstractAPI instance.
     * @throws IllegalStateException If the API is not loaded.
     */
    public static AltarACAbstractAPI get() {
        if (instance == null) {
            throw new IllegalStateException("AltarACAPI is not loaded. Ensure the AltarAC mod is installed and initialized.");
        }
        return instance;
    }

    /**
     * Gets the AltarACAPI instance asynchronously.
     * The returned CompletableFuture will complete when the AltarACAPI instance is available.
     * If the API is already loaded, the future will complete immediately.
     * If the API fails to load (e.g., the mod is not installed), the future will complete exceptionally.
     *
     * @return A CompletableFuture that completes with the AltarACAbstractAPI instance.
     */
    public static CompletableFuture<AltarACAbstractAPI> getAsync() {
        if (instance != null) {
            // If the instance is already loaded, return a completed future
            return CompletableFuture.completedFuture(instance);
        }
        return futureInstance;
    }
}