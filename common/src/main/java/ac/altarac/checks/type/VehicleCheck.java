package ac.altarac.checks.type;

import ac.altarac.api.AbstractCheck;
import ac.altarac.utils.anticheat.update.VehiclePositionUpdate;

public interface VehicleCheck extends AbstractCheck {

    void process(final VehiclePositionUpdate vehicleUpdate);
}
