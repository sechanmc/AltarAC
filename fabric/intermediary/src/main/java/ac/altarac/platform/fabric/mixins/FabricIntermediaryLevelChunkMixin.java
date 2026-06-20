package ac.altarac.platform.fabric.mixins;

import ac.altarac.platform.api.world.PlatformChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelChunk.class)
@Implements(@Interface(iface = PlatformChunk.class, prefix = "AltarAC$"))
abstract class FabricIntermediaryLevelChunkMixin {
    @Unique
    private static final BlockPos.MutableBlockPos AltarAC$sharedPos = new BlockPos.MutableBlockPos();

    public int AltarAC$getBlockID(int x, int y, int z) {
        LevelChunk chunk = (LevelChunk) (Object) this;
        AltarAC$sharedPos.set(chunk.getPos().getMinBlockX() + x, y, chunk.getPos().getMinBlockZ() + z);
        return Block.getId(chunk.getBlockState(AltarAC$sharedPos));
    }
}
