package ac.altarac.utils.inventory.inventory;

import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.inventory.Inventory;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;

public class NotImplementedMenu extends AbstractContainerMenu {
    public NotImplementedMenu(AltarACPlayer player, Inventory playerInventory) {
        super(player, playerInventory);
        player.inventory.isPacketInventoryActive = false;
        player.inventory.needResend = true;
    }

    @Override
    public void doClick(int button, int slotID, WrapperPlayClientClickWindow.WindowClickType clickType) {

    }
}
