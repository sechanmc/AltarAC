package ac.altarac.api.plugin;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface AltarACPluginDescription {
    String getVersion();

    String getDescription();

    public @NotNull Collection<String> getAuthors();
}
