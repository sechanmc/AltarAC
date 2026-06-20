package ac.altarac.checks.impl.crash;

import ac.altarac.api.storage.verbose.Verbose;
import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.checks.type.PacketCheck;
import ac.altarac.player.AltarACPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;

@CheckData(name = "CrashE", stableKey = "AltarAC.crash.low_view_distance", description = "Sent a client view distance below the minimum allowed value")
public class CrashE extends Check implements PacketCheck {
    private static final Verbose V = Verbose.of("distance={sint}");

    public CrashE(AltarACPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
            WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);
            int viewDistance = wrapper.getViewDistance();
            if (viewDistance < 2) {
                flag(V.write(verbose()).sint(viewDistance));
                wrapper.setViewDistance(2);
            }
        }
    }

}