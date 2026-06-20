package ac.altarac.checks.impl.badpackets;

import ac.altarac.api.storage.verbose.Verbose;
import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.checks.type.PacketCheck;
import ac.altarac.player.AltarACPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "BadPacketsD", stableKey = "AltarAC.badpackets.invalid_pitch", description = "Sent an invalid rotation pitch outside the -90 to 90 range")
public class BadPacketsD extends Check implements PacketCheck {
    private static final Verbose V = Verbose.of("pitch={f32}");

    public BadPacketsD(AltarACPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (player.packetStateData.lastPacketWasTeleport) return;

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            final float pitch = new WrapperPlayClientPlayerFlying(event).getLocation().getPitch();
            if (pitch > 90 || pitch < -90) {
                // Ban.
                if (flag(V.write(verbose()).f32(pitch)) && shouldModifyPackets()) {
                    // prevent other checks from using an invalid pitch
                    if (player.pitch > 90) player.pitch = 90;
                    if (player.pitch < -90) player.pitch = -90;

                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }
}