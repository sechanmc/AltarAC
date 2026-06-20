package ac.altarac.utils.collisions.datatypes;

import ac.altarac.player.AltarACPlayer;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;

public interface CollisionFactory {
    CollisionBox fetch(AltarACPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z);
}
