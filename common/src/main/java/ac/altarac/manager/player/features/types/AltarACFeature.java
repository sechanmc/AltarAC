package ac.altarac.manager.player.features.types;

import ac.altarac.api.config.ConfigManager;
import ac.altarac.api.feature.FeatureState;
import ac.altarac.player.AltarACPlayer;

public interface AltarACFeature {
    String getName();

    void setState(AltarACPlayer player, ConfigManager config, FeatureState state);

    boolean isEnabled(AltarACPlayer player);

    boolean isEnabledInConfig(AltarACPlayer player, ConfigManager config);
}
