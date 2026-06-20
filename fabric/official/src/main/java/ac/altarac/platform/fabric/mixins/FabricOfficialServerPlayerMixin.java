package ac.altarac.platform.fabric.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(ServerPlayer.class)
@Implements(@Interface(iface = ac.altarac.platform.fabric.inject.FabricServerPlayerHandle.class, prefix = "AltarAC$"))
abstract class FabricOfficialServerPlayerMixin {

    public boolean AltarAC$isSneaking() {
        return ((ServerPlayer) (Object) this).isShiftKeyDown();
    }

    public void AltarAC$setSneaking(boolean sneaking) {
        ((ServerPlayer) (Object) this).setShiftKeyDown(sneaking);
    }

    public boolean AltarAC$isDead() {
        return ((ServerPlayer) (Object) this).isDeadOrDying();
    }

    public void AltarAC$sendSystemText(Object nativeComponent) {
        ((ServerPlayer) (Object) this).sendSystemMessage((Component) nativeComponent, false);
    }

    public boolean AltarAC$isDisconnected() {
        return ((ServerPlayer) (Object) this).hasDisconnected();
    }

    public String AltarAC$usernameString() {
        return ((ServerPlayer) (Object) this).getName().getString();
    }

    public void AltarAC$broadcastInventoryChanges() {
        ((ServerPlayer) (Object) this).containerMenu.broadcastChanges();
    }

    public double AltarAC$posX() {
        return ((ServerPlayer) (Object) this).getX();
    }

    public double AltarAC$posY() {
        return ((ServerPlayer) (Object) this).getY();
    }

    public double AltarAC$posZ() {
        return ((ServerPlayer) (Object) this).getZ();
    }

    public UUID AltarAC$uuid() {
        return ((ServerPlayer) (Object) this).getUUID();
    }

    public Object AltarAC$vehicleEntity() {
        return ((ServerPlayer) (Object) this).getVehicle();
    }

    public Object AltarAC$gameMode() {
        return ((ServerPlayer) (Object) this).gameMode.getGameModeForPlayer();
    }

    public Object AltarAC$heldItemStack() {
        return ((ServerPlayer) (Object) this).inventory.getSelectedItem();
    }

    public Object AltarAC$inventoryItemAt(int slot) {
        return ((ServerPlayer) (Object) this).inventory.getItem(slot);
    }

    public Object AltarAC$usedItemHand() {
        return ((ServerPlayer) (Object) this).getUsedItemHand();
    }

    public int AltarAC$inventorySlotCount() {
        return ((ServerPlayer) (Object) this).inventory.getContainerSize();
    }

    // NOTE: isUsingItem()/stopUsingItem() are intentionally NOT bodied here. On the mojmap
    // runtime the vanilla ServerPlayer methods of the same name satisfy the injected interface
    // directly; a AltarAC$ body would graft a same-named method and self-recurse (StackOverflow).
    // The intermediary mixin DOES body them (vanilla is method_6115/method_6021 there, so no clash).
}
