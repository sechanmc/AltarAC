package ac.altarac.manager.player.features.types;

import ac.altarac.api.config.ConfigManager;
import ac.altarac.api.feature.FeatureState;
import ac.altarac.player.AltarACPlayer;

public class ExemptElytraFeature implements AltarACFeature {

    @Override
    public String getName() {
        return "ExemptElytra";
    }

    @Override
    public void setState(AltarACPlayer player, ConfigManager config, FeatureState state) {
        switch (state) {
            case ENABLED -> player.setExemptElytra(true);
            case DISABLED -> player.setExemptElytra(false);
            default -> player.setExemptElytra(isEnabledInConfig(player, config));
        }
    }

    @Override
    public boolean isEnabled(AltarACPlayer player) {
        return player.isExemptElytra();
    }

    @Override
    public boolean isEnabledInConfig(AltarACPlayer player, ConfigManager config) {
        return config.getBooleanElse("exempt-elytra", false);
    }

}
