package ac.altarac.platform.fabric.mixins;

import ac.altarac.platform.api.world.PlatformChunk;
import ac.altarac.platform.api.world.PlatformWorld;
import ac.altarac.platform.fabric.AltarACFabricOfficialLoaderPlugin;
import ac.altarac.platform.fabric.utils.world.FabricOfficialLevelChunkUtil;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.UUID;

@Mixin(Level.class)
@Implements(@Interface(iface = PlatformWorld.class, prefix = "AltarAC$"))
abstract class FabricOfficialLevelMixin implements LevelAccessor {

    @Shadow
    public abstract ResourceKey<Level> dimension();

    public boolean AltarAC$isChunkLoaded(int chunkX, int chunkZ) {
        return FabricOfficialLevelChunkUtil.hasChunkAt((Level) (Object) this, chunkX, chunkZ);
    }

    public WrappedBlockState AltarAC$getBlockAt(int x, int y, int z) {
        return WrappedBlockState.getByGlobalId(
                Block.getId(getBlockState(new BlockPos(x, y, z)))
        );
    }

    public String AltarAC$getName() {
        return this.dimension().identifier().toString();
    }

    public @Nullable UUID AltarAC$getUID() {
        return null;
    }

    public PlatformChunk AltarAC$getChunkAt(int currChunkX, int currChunkZ) {
        return (PlatformChunk) getChunk(currChunkX, currChunkZ);
    }

    public boolean AltarAC$isLoaded() {
        return AltarACFabricOfficialLoaderPlugin.FABRIC_SERVER.getLevel(this.dimension()) != null;
    }
}
