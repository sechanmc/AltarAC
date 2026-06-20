package ac.altarac.checks.impl.vehicle;

import ac.altarac.api.storage.verbose.Verbose;
import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.checks.impl.verbose.VerboseCodecs;
import ac.altarac.checks.type.PacketCheck;
import ac.altarac.player.AltarACPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

@CheckData(name = "VehicleE", stableKey = "AltarAC.vehicle.spoofed_boat", experimental = true, description = "Sent boat paddle states while not in a boat")
public class VehicleE extends Check implements PacketCheck {
    private static final Verbose V = Verbose.of("vehicle=[{entity}|null]");

    public VehicleE(AltarACPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.STEER_BOAT) {
            final EntityType vehicle = player.getVehicleType();

            if (!EntityTypes.isTypeInstanceOf(vehicle, EntityTypes.BOAT)) {
                if (flag(V.write(verbose()).bool(vehicle != null).uint(vehicle == null ? 0 : VerboseCodecs.entity(vehicle, player.getClientVersion()))) && shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }
}
