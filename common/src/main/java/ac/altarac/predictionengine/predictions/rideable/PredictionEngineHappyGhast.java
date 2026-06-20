package ac.altarac.predictionengine.predictions.rideable;

import ac.altarac.player.AltarACPlayer;
import ac.altarac.predictionengine.predictions.PredictionEngineNormal;
import ac.altarac.predictionengine.predictions.input.Input;
import ac.altarac.utils.data.VectorData;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class PredictionEngineHappyGhast extends PredictionEngineNormal {
    private final Input movementVector;
    private final double multiplier;

    @Override
    public void endOfTick(AltarACPlayer player, double delta) {
        for (VectorData vector : player.getPossibleVelocitiesMinusKnockback()) {
            vector.vector.setX(vector.vector.getX() * multiplier);
            vector.vector.setY(vector.vector.getY() * multiplier);
            vector.vector.setZ(vector.vector.getZ() * multiplier);
        }
    }

    @Override
    public List<VectorData> applyInputsToVelocityPossibilities(AltarACPlayer player, Set<VectorData> possibleVectors, float speed) {
        return PredictionEngineRideableUtils.applyInputsToVelocityPossibilities(movementVector, player, possibleVectors, speed);
    }

}
