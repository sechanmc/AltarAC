package ac.altarac.platform.fabric.mixins;

import ac.altarac.platform.fabric.inject.FabricEntityHandle;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(Entity.class)
@Implements(@Interface(iface = FabricEntityHandle.class, prefix = "AltarAC$"))
abstract class FabricOfficialEntityMixin {

    public UUID AltarAC$fabricEntityUuid() {
        return ((Entity) (Object) this).getUUID();
    }

    public boolean AltarAC$fabricEjectPassengers() {
        Entity entity = (Entity) (Object) this;
        if (entity.isVehicle()) {
            entity.ejectPassengers();
            return true;
        }
        return false;
    }

    public Object AltarAC$fabricWorld() {
        return ((Entity) (Object) this).level;
    }

    public double AltarAC$fabricPosX() {
        return ((Entity) (Object) this).getX();
    }

    public double AltarAC$fabricPosY() {
        return ((Entity) (Object) this).getY();
    }

    public double AltarAC$fabricPosZ() {
        return ((Entity) (Object) this).getZ();
    }

    public float AltarAC$fabricYaw(float partialTick) {
        return ((Entity) (Object) this).getViewYRot(partialTick);
    }

    public float AltarAC$fabricPitch(float partialTick) {
        return ((Entity) (Object) this).getViewXRot(partialTick);
    }
}
