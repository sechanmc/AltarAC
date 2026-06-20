package ac.altarac.utils.blockplace;

import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.anticheat.update.BlockPlace;

public interface BlockPlaceFactory {
    void applyBlockPlaceToWorld(AltarACPlayer player, BlockPlace place);
}
