package ac.altarac.platform.fabric.mc1171.entity;

import ac.altarac.platform.fabric.mc1161.entity.Fabric1161AltarACEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class Fabric1170AltarACEntity extends Fabric1161AltarACEntity {

    public Fabric1170AltarACEntity(Entity entity) {
        super(entity);
    }

    @Override
    public boolean isDead() {
        return this.entity instanceof LivingEntity living ? living.isDeadOrDying() : this.entity.isRemoved();
    }
}
