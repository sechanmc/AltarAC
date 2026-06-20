package ac.altarac.platform.fabric.mc261;

import ac.altarac.platform.api.sender.Sender;
import ac.altarac.platform.fabric.AbstractFabricPlatformServer;
import ac.altarac.platform.fabric.AltarACFabricOfficialLoaderPlugin;
import ac.altarac.platform.fabric.player.FabricOfflineProfile;
import com.mojang.authlib.GameProfile;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import org.jetbrains.annotations.Nullable;

public class Fabric261PlatformServer extends AbstractFabricPlatformServer {
    @Override
    public int getOperatorPermissionLevel() {
        return AltarACFabricOfficialLoaderPlugin.FABRIC_SERVER.operatorUserPermissions().level().id();
    }

    @Override
    public boolean hasPermission(Sender sender, int level) {
        CommandSourceStack stack = (CommandSourceStack) sender;
        return stack.permissions().hasPermission(
                new Permission.HasCommandLevel(PermissionLevel.byId(level)));
    }

    @Override
    public double getTPS() {
        return Math.min(1000.0 / AltarACFabricOfficialLoaderPlugin.FABRIC_SERVER.getCurrentSmoothedTickTime(),
                AltarACFabricOfficialLoaderPlugin.FABRIC_SERVER.tickRateManager().tickrate());
    }

    @Override
    public void dispatchCommand(Sender sender, String command) {
        CommandSourceStack stack = (CommandSourceStack) AltarACFabricOfficialLoaderPlugin.LOADER.getFabricSenderFactory().unwrap(sender);
        AltarACFabricOfficialLoaderPlugin.FABRIC_SERVER.getCommands().performPrefixedCommand(stack, command);
    }

    @Override
    public @Nullable FabricOfflineProfile getProfileByName(String name) {
        GameProfile profile = AltarACFabricOfficialLoaderPlugin.FABRIC_SERVER.services().profileResolver().fetchByName(name).orElse(null);
        return profile != null ? new FabricOfflineProfile(profile.id(), profile.name()) : null;
    }
}
