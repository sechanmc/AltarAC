package ac.altarac.manager.player.features.types;

import ac.altarac.api.config.ConfigManager;
import ac.altarac.api.feature.FeatureState;
import ac.altarac.player.AltarACPlayer;

public class ForceSlowMovementFeature implements AltarACFeature {

    @Override
    public String getName() {
        return "ForceSlowMovement";
    }

    @Override
    public void setState(AltarACPlayer player, ConfigManager config, FeatureState state) {
        switch (state) {
            case ENABLED -> player.setForceSlowMovement(true);
            case DISABLED -> player.setForceSlowMovement(false);
            default -> player.setForceSlowMovement(isEnabledInConfig(player, config));
        }
    }

    @Override
    public boolean isEnabled(AltarACPlayer player) {
        return player.isForceSlowMovement();
    }

    @Override
    public boolean isEnabledInConfig(AltarACPlayer player, ConfigManager config) {
        return config.getBooleanElse("force-slow-movement", true);
    }

}
