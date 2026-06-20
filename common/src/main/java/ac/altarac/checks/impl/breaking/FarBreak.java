package ac.altarac.checks.impl.breaking;

import ac.altarac.api.storage.verbose.Verbose;
import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.checks.type.BlockBreakCheck;
import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.anticheat.update.BlockBreak;
import ac.altarac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.altarac.utils.math.Vector3dm;
import ac.altarac.utils.math.VectorUtils;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;

@CheckData(name = "FarBreak", stableKey = "AltarAC.breaking.far_break", description = "Breaking blocks too far away", experimental = true)
public class FarBreak extends Check implements BlockBreakCheck {
    private static final Verbose V = Verbose.of("distance={f64:%.2f}");

    public FarBreak(AltarACPlayer player) {
        super(player);
    }

    @Override
    public void onBlockBreak(BlockBreak blockBreak) {
        if (!player.cameraEntity.isSelf() || player.inVehicle() || blockBreak.action == DiggingAction.CANCELLED_DIGGING)
            return; // falses

        double min = Double.MAX_VALUE;
        for (double d : player.getPossibleEyeHeights()) {
            SimpleCollisionBox box = new SimpleCollisionBox(blockBreak.position);
            Vector3dm best = VectorUtils.cutBoxToVector(player.x, player.y + d, player.z, box);
            min = Math.min(min, best.distanceSquared(player.x, player.y + d, player.z));
        }

        // getPickRange() determines this?
        // With 1.20.5+ the new attribute determines creative mode reach using a modifier
        double maxReach = player.compensatedEntities.self.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        if (player.packetStateData.didLastMovementIncludePosition || player.canSkipTicks()) {
            double threshold = player.getMovementThreshold();
            maxReach += Math.hypot(threshold, threshold);
        }

        if (min > maxReach * maxReach) {
            double distance = Math.sqrt(min);
            if (flag(V.write(verbose()).f64(distance)) && shouldModifyPackets()) {
                blockBreak.cancel();
            }
        }
    }
}
