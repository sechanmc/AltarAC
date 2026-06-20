package ac.altarac.checks.type;

import ac.altarac.api.AbstractCheck;
import ac.altarac.utils.anticheat.update.RotationUpdate;

public interface RotationCheck extends AbstractCheck {

    default void process(final RotationUpdate rotationUpdate) {
    }
}
