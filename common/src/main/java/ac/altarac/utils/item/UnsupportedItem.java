package ac.altarac.utils.item;

import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.latency.CompensatedWorld;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;

public class UnsupportedItem extends ItemBehaviour {

    public static final UnsupportedItem INSTANCE = new UnsupportedItem();

    @Override
    public boolean canUse(ItemStack item, CompensatedWorld world, AltarACPlayer player, InteractionHand hand) {
        return false;
    }

}
