package ac.altarac.checks.impl.scaffolding;

import ac.altarac.api.storage.verbose.Verbose;
import ac.altarac.checks.CheckData;
import ac.altarac.checks.type.BlockPlaceCheck;
import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.anticheat.update.BlockPlace;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

@CheckData(name = "InvalidPlaceB", stableKey = "AltarAC.scaffolding.invalid_place_b", description = "Sent impossible block face id")
public class InvalidPlaceB extends BlockPlaceCheck {
    private static final Verbose V = Verbose.of("direction={sint}");

    public InvalidPlaceB(AltarACPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(final BlockPlace place) {
        if (place.getFaceId() == 255 && PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8)) {
            return;
        }

        if (place.getFaceId() < 0 || place.getFaceId() > 5) {
            // ban
            int direction = place.getFaceId();
            if (flag(V.write(verbose()).sint(direction)) && shouldModifyPackets() && shouldCancel()) {
                place.resync();
            }
        }
    }
}
