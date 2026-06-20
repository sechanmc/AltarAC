package ac.altarac.utils.inventory.slot;

import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.inventory.InventoryStorage;
import com.github.retrooper.packetevents.protocol.item.ItemStack;

public class ResultSlot extends Slot {

    public ResultSlot(InventoryStorage container, int slot) {
        super(container, slot);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public void onTake(AltarACPlayer player, ItemStack itemStack) {
        // Resync the player's inventory
    }
}
