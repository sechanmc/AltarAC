package ac.altarac.checks.impl.badpackets;

import ac.altarac.api.storage.verbose.Verbose;
import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.checks.type.PacketCheck;
import ac.altarac.player.AltarACPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;

@CheckData(name = "BadPacketsA", stableKey = "AltarAC.badpackets.duplicate_slot", description = "Sent duplicate slot id")
public class BadPacketsA extends Check implements PacketCheck {
    private static final Verbose V = Verbose.of("slot={sint}");

    private int lastSlot = -1;

    public BadPacketsA(final AltarACPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            final int slot = new WrapperPlayClientHeldItemChange(event).getSlot();

            if (slot == lastSlot) {
                if (flag(V.write(verbose()).sint(slot)) && shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }

            lastSlot = slot;
        }
    }
}
