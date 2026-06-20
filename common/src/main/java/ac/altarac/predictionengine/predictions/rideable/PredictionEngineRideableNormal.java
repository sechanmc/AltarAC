package ac.altarac.predictionengine.predictions.rideable;

import ac.altarac.player.AltarACPlayer;
import ac.altarac.predictionengine.predictions.PredictionEngineNormal;
import ac.altarac.predictionengine.predictions.input.Input;
import ac.altarac.utils.data.VectorData;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class PredictionEngineRideableNormal extends PredictionEngineNormal {
    private final Input movementVector;

    @Override
    public void addJumpsToPossibilities(AltarACPlayer player, Set<VectorData> existingVelocities) {
        PredictionEngineRideableUtils.handleJumps(player, existingVelocities);
    }

    @Override
    public List<VectorData> applyInputsToVelocityPossibilities(AltarACPlayer player, Set<VectorData> possibleVectors, float speed) {
        return PredictionEngineRideableUtils.applyInputsToVelocityPossibilities(movementVector, player, possibleVectors, speed);
    }

}
