package ac.altarac.checks.impl.movement;

import ac.altarac.checks.Check;
import ac.altarac.checks.type.PositionCheck;
import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.anticheat.update.PositionUpdate;

public class PredictionRunner extends Check implements PositionCheck {
    public PredictionRunner(AltarACPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPositionUpdate(final PositionUpdate positionUpdate) {
        if (!player.inVehicle()) {
            player.movementCheckRunner.processAndCheckMovementPacket(positionUpdate);
        }
    }
}
