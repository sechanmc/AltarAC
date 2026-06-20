package ac.altarac.platform.fabric.mc1161;

import ac.altarac.platform.api.sender.Sender;
import ac.altarac.platform.fabric.AbstractFabricPlatformServer;
import ac.altarac.platform.fabric.AltarACFabricIntermediaryLoaderPlugin;
import ac.altarac.platform.fabric.player.FabricOfflineProfile;
import com.mojang.authlib.GameProfile;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

public class Fabric1140PlatformServer extends AbstractFabricPlatformServer {

    @Override
    public int getOperatorPermissionLevel() {
        return AltarACFabricIntermediaryLoaderPlugin.FABRIC_SERVER.getOperatorUserPermissionLevel();
    }

    @Override
    public boolean hasPermission(Sender sender, int level) {
        return ((CommandSourceStack) sender).hasPermission(level);
    }

    @Override
    public void dispatchCommand(Sender sender, String command) {
        CommandSourceStack commandSource = AltarACFabricIntermediaryLoaderPlugin.LOADER.getFabricSenderFactory().unwrap(sender);
        AltarACFabricIntermediaryLoaderPlugin.FABRIC_SERVER.getCommands().performCommand(commandSource, command);
    }

    @Override
    public double getTPS() {
        return Math.min(1000.0 / AltarACFabricIntermediaryLoaderPlugin.FABRIC_SERVER.getAverageTickTime(), 20.0);
    }

    @Override
    public @Nullable FabricOfflineProfile getProfileByName(String name) {
        GameProfile profile = AltarACFabricIntermediaryLoaderPlugin.FABRIC_SERVER.getProfileCache().get(name);
        return profile != null ? new FabricOfflineProfile(profile.getId(), profile.getName()) : null;
    }
}
