package ac.altarac.checks.impl.baritone;

import ac.altarac.api.storage.verbose.Verbose;
import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.checks.impl.aim.processor.AimProcessor;
import ac.altarac.checks.type.RotationCheck;
import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.anticheat.update.RotationUpdate;
import ac.altarac.utils.data.HeadRotation;
import ac.altarac.utils.math.AltarACMath;

// This check has been patched by Baritone for a long time, and it also seems to false with cinematic camera now, so it is disabled.
@CheckData(name = "Baritone", stableKey = "AltarAC.baritone.baritone", description = "Detected Baritone like behavior")
public class Baritone extends Check implements RotationCheck {
    private static final Verbose V = Verbose.of("divisor={f64}");

    private int verbose;

    public Baritone(AltarACPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        final HeadRotation from = rotationUpdate.getFrom();
        final HeadRotation to = rotationUpdate.getTo();

        final float deltaPitch = Math.abs(to.pitch() - from.pitch());

        // Baritone works with small degrees, limit to 1 degree to pick up on baritone slightly moving aim to bypass anticheats
        if (rotationUpdate.getDeltaXRot() == 0 && deltaPitch > 0 && deltaPitch < 1 && Math.abs(to.pitch()) != 90.0f) {
            if (rotationUpdate.getProcessor().divisorY < AltarACMath.MINIMUM_DIVISOR) {
                verbose++;
                if (verbose > 8) {
                    double divisor = AimProcessor.convertToSensitivity(rotationUpdate.getProcessor().divisorX);
                    flag(V.write(verbose()).f64(divisor));
                }
            } else {
                verbose = 0;
            }
        }
    }
}