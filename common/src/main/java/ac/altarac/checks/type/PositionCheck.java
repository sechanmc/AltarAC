package ac.altarac.checks.type;

import ac.altarac.api.AbstractCheck;
import ac.altarac.utils.anticheat.update.PositionUpdate;

public interface PositionCheck extends AbstractCheck {

    default void onPositionUpdate(final PositionUpdate positionUpdate) {
    }
}
