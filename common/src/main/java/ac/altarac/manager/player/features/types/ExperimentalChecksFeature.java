package ac.altarac.manager.player.features.types;

import ac.altarac.api.config.ConfigManager;
import ac.altarac.api.feature.FeatureState;
import ac.altarac.player.AltarACPlayer;

public class ExperimentalChecksFeature implements AltarACFeature {

    @Override
    public String getName() {
        return "ExperimentalChecks";
    }

    @Override
    public void setState(AltarACPlayer player, ConfigManager config, FeatureState state) {
        switch (state) {
            case ENABLED -> player.setExperimentalChecks(true);
            case DISABLED -> player.setExperimentalChecks(false);
            default -> player.setExperimentalChecks(isEnabledInConfig(player, config));
        }
    }

    @Override
    public boolean isEnabled(AltarACPlayer player) {
        return player.isExperimentalChecks();
    }

    @Override
    public boolean isEnabledInConfig(AltarACPlayer player, ConfigManager config) {
        return config.getBooleanElse("experimental-checks", false);
    }

}
