package ac.altarac.checks.impl.vehicle;

import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.player.AltarACPlayer;

@CheckData(name = "VehicleC", stableKey = "AltarAC.vehicle.vehicle_control", description = "Moved a vehicle in a way that did not match predicted vehicle control")
public class VehicleC extends Check {
    public VehicleC(AltarACPlayer player) {
        super(player);
    }
}
