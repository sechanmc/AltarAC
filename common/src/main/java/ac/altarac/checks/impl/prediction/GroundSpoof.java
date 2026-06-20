package ac.altarac.checks.impl.prediction;

import ac.altarac.api.storage.verbose.Verbose;
import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.checks.type.PostPredictionCheck;
import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;

@CheckData(name = "GroundSpoof", stableKey = "AltarAC.groundspoof.fake", description = "Claimed to be on ground when predicted otherwise", setback = 10, decay = 0.01)
public class GroundSpoof extends Check implements PostPredictionCheck {
    private static final Verbose V = Verbose.of("claimed {bool}");

    public GroundSpoof(AltarACPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        // Exemptions
        // Don't check players in spectator
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_8) && player.gamemode == GameMode.SPECTATOR)
            return;
        // And don't check this long list of ground exemptions
        if (player.exemptOnGround() || !predictionComplete.isChecked()) return;
        // Don't check if the player was on a ghost block
        if (player.getSetbackTeleportUtil().blockOffsets) return;
        // Viaversion sends wrong ground status... (doesn't matter but is annoying)
        if (player.packetStateData.lastPacketWasTeleport) return;

        boolean claimed = player.clientClaimsLastOnGround;
        if (claimed != player.onGround) {
            flagWithSetback(V.write(verbose()).bool(claimed));
            player.checkManager.getNoFall().flipPlayerGroundStatus = true;
        }
    }
}
