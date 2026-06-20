package ac.altarac.utils.data.packetentity;

import ac.altarac.player.AltarACPlayer;
import ac.altarac.utils.data.VectorData;

import java.util.Set;

public interface JumpableEntity {

    boolean isJumping();

    void setJumping(boolean jumping);

    float getJumpPower();

    void setJumpPower(float jumpPower);

    boolean canPlayerJump(AltarACPlayer player);

    boolean hasSaddle();

    void executeJump(AltarACPlayer player, Set<VectorData> possibleVectors);

}
