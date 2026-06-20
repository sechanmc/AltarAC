package ac.altarac.internal.plugin.resolver;

import ac.altarac.api.plugin.AltarACPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * A functional interface responsible for attempting to resolve a generic context object
 * into a {@link AltarACPlugin}.
 * <p>
 * Implementations of this are provided by the core AltarAC platform module (e.g., for Bukkit, Fabric)
 * and registered with the central AltarACExtensionManager.
 */
@FunctionalInterface
public interface AltarACExtensionResolver {

    /**
     * Attempts to resolve the given context object into a AltarACPlugin.
     *
     * @param context The context object to resolve (e.g., a Bukkit Plugin, a Plugin Class, a Fabric Mod).
     * @return A AltarACPlugin if this resolver supports the context type, otherwise null.
     */
    @Nullable AltarACPlugin resolve(@NotNull Object context);

}
