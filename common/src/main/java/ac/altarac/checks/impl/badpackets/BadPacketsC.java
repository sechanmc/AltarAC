package ac.altarac.checks.impl.badpackets;

import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.checks.type.PacketCheck;
import ac.altarac.player.AltarACPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import org.jetbrains.annotations.NotNull;

@CheckData(name = "BadPacketsC", stableKey = "AltarAC.badpackets.wake_not_sleeping", description = "Tried to wake up while not sleeping", experimental = true)
public class BadPacketsC extends Check implements PacketCheck {
    public BadPacketsC(@NotNull AltarACPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION
                && new WrapperPlayClientEntityAction(event).getAction() == WrapperPlayClientEntityAction.Action.LEAVE_BED
                && !player.isInBed) {
            flag();
        }
    }
}
