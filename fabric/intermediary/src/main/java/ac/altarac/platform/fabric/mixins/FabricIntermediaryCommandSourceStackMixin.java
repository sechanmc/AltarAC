package ac.altarac.platform.fabric.mixins;

import ac.altarac.AltarACAPI;
import ac.altarac.platform.api.player.PlatformPlayer;
import ac.altarac.platform.api.sender.Sender;
import ac.altarac.platform.fabric.AltarACFabricIntermediaryLoaderPlugin;
import ac.altarac.platform.fabric.sender.FabricIntermediarySenderFactory;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(CommandSourceStack.class)
@Implements(@Interface(iface = Sender.class, prefix = "AltarAC$"))
abstract class FabricIntermediaryCommandSourceStackMixin {

    @Unique
    private CommandSourceStack AltarAC$self() {
        return (CommandSourceStack) (Object) this;
    }

    @Unique
    private FabricIntermediarySenderFactory AltarAC$factory() {
        return AltarACFabricIntermediaryLoaderPlugin.LOADER.getFabricSenderFactory();
    }

    public UUID AltarAC$getUniqueId() {
        return AltarAC$factory().getUniqueId(AltarAC$self());
    }

    public String AltarAC$getName() {
        return AltarAC$factory().getName(AltarAC$self());
    }

    public void AltarAC$sendMessage(String message) {
        AltarAC$factory().sendMessage(AltarAC$self(), message);
    }

    public void AltarAC$sendMessage(Component message) {
        AltarAC$factory().sendMessage(AltarAC$self(), message);
    }

    public boolean AltarAC$hasPermission(String permission) {
        return AltarAC$factory().hasPermission(AltarAC$self(), permission);
    }

    public boolean AltarAC$hasPermission(String permission, boolean defaultIfUnset) {
        return AltarAC$factory().hasPermission(AltarAC$self(), permission, defaultIfUnset);
    }

    public void AltarAC$performCommand(String commandLine) {
        AltarAC$factory().performCommand(AltarAC$self(), commandLine);
    }

    public boolean AltarAC$isConsole() {
        return AltarAC$factory().isConsole(AltarAC$self());
    }

    public Object AltarAC$getNativeSender() {
        return AltarAC$self();
    }

    public @Nullable PlatformPlayer AltarAC$getPlatformPlayer() {
        return AltarACAPI.INSTANCE.getPlatformPlayerFactory().getFromUUID(AltarAC$getUniqueId());
    }
}
