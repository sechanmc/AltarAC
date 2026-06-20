package ac.altarac.checks.impl.movement;

import ac.altarac.checks.Check;
import ac.altarac.checks.type.VehicleCheck;
import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.anticheat.update.PositionUpdate;
import ac.altarac.utils.anticheat.update.VehiclePositionUpdate;

public class VehiclePredictionRunner extends Check implements VehicleCheck {
    public VehiclePredictionRunner(AltarACPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(final VehiclePositionUpdate vehicleUpdate) {
        // Vehicle onGround = false always
        // We don't do vehicle setbacks because vehicle netcode sucks.
        player.movementCheckRunner.processAndCheckMovementPacket(new PositionUpdate(vehicleUpdate.from(), vehicleUpdate.to(), false, null, null, vehicleUpdate.isTeleport()));
    }
}
