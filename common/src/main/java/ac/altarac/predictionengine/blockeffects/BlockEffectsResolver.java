package ac.altarac.predictionengine.blockeffects;

import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.math.Vector3dm;

import java.util.List;

public interface BlockEffectsResolver {

    void applyEffectsFromBlocks(AltarACPlayer player, Vector3dm clientVelocity, boolean onlyApplyVelocity, List<AltarACPlayer.Movement> movements);

}
