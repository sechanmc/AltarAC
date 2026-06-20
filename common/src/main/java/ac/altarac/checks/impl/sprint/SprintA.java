package ac.altarac.checks.impl.sprint;

import ac.altarac.api.storage.verbose.Verbose;
import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.checks.type.PostPredictionCheck;
import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;

@CheckData(name = "SprintA", stableKey = "AltarAC.sprint.hunger", description = "Sprinting with too low hunger", setback = 0)
public class SprintA extends Check implements PostPredictionCheck {
    private static final Verbose V = Verbose.of("hunger={uint}");

    public SprintA(AltarACPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(PredictionComplete predictionComplete) {
        if (!predictionComplete.isChecked()) return;

        // Players can sprint if they're able to fly
        // Players can also sprint if they are on a camel, regardless of their hunger level
        if (player.canFly || EntityTypes.isTypeInstanceOf(player.getVehicleType(), EntityTypes.CAMEL)) return;

        if (player.food <= 6.0F) {
            if (player.isSprinting) {
                if (flag(V.write(verbose()).uint(player.food))) {
                    if (shouldModifyPackets()) {
                        player.onPacketCancel();
                    }
                    setbackIfAboveSetbackVL();
                }
            } else {
                reward();
            }
        }
    }
}
