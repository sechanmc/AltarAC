package ac.altarac.checks.impl.packetorder;

import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.checks.type.PostPredictionCheck;
import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow.WindowClickType;

@CheckData(name = "PacketOrderA", stableKey = "AltarAC.packetorder.window_click_order", description = "Sent pickup and quick-move inventory clicks in an invalid order", experimental = true)
public class PacketOrderA extends Check implements PostPredictionCheck {
    public PacketOrderA(final AltarACPlayer player) {
        super(player);
    }

    private int invalid;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            final WindowClickType clickType = new WrapperPlayClientClickWindow(event).getWindowClickType();

            if ((clickType == WindowClickType.PICKUP || clickType == WindowClickType.PICKUP_ALL) && player.packetOrderProcessor.isQuickMoveClicking()
                    || clickType == WindowClickType.QUICK_MOVE && player.packetOrderProcessor.isPickUpClicking()) {
                if (!player.canSkipTicks()) {
                    if (flag() && shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                } else {
                    invalid++;
                }
            }
        }
    }

    @Override
    public void onPredictionComplete(PredictionComplete predictionComplete) {
        if (!player.canSkipTicks()) return;

        if (player.isTickingReliablyFor(3)) {
            for (; invalid >= 1; invalid--) {
                flag();
            }
        }

        invalid = 0;
    }
}
