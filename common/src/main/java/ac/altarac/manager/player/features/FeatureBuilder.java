package ac.altarac.manager.player.features;

import ac.altarac.manager.player.features.types.AltarACFeature;
import ac.altarac.utils.anticheat.LogUtil;
import com.google.common.collect.ImmutableMap;

import java.util.regex.Pattern;

public class FeatureBuilder {

    private static final Pattern VALID = Pattern.compile("[a-zA-Z0-9_]{1,64}");
    private final ImmutableMap.Builder<String, AltarACFeature> mapBuilder = ImmutableMap.builder();

    public <T extends AltarACFeature> void register(T feature) {
        if (!VALID.matcher(feature.getName()).matches()) {
            LogUtil.error("Invalid feature name: " + feature.getName());
            return;
        }
        mapBuilder.put(feature.getName(), feature);
    }

    public ImmutableMap<String, AltarACFeature> buildMap() {
        return mapBuilder.build();
    }

}
