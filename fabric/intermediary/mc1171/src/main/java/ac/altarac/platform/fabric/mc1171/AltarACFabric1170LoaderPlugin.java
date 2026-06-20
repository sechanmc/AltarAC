package ac.altarac.platform.fabric.mc1171;

import ac.altarac.platform.fabric.AbstractFabricPlatformServer;
import ac.altarac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.altarac.platform.fabric.AltarACFabricIntermediaryLoaderPlugin;
import ac.altarac.platform.fabric.mc1171.player.Fabric1170PlatformPlayer;
import ac.altarac.platform.fabric.mc1161.Fabric1140PlatformServer;
import ac.altarac.platform.fabric.mc1161.player.Fabric1161PlatformInventory;
import ac.altarac.platform.fabric.mc1171.entity.Fabric1170AltarACEntity;
import ac.altarac.platform.fabric.mc1161.util.convert.Fabric1140ConversionUtil;
import ac.altarac.platform.fabric.mc1161.util.convert.Fabric1161MessageUtil;
import ac.altarac.platform.fabric.player.FabricPlatformPlayerFactory;
import ac.altarac.platform.fabric.utils.convert.IFabricConversionUtil;
import ac.altarac.platform.fabric.utils.message.IFabricMessageUtil;
import ac.altarac.utils.lazy.LazyHolder;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;


public class AltarACFabric1170LoaderPlugin extends AltarACFabricIntermediaryLoaderPlugin {

    public AltarACFabric1170LoaderPlugin() {
        this(AltarACFabricIntermediaryLoaderPlugin::createCommandArguments,
                new FabricPlatformPlayerFactory(
                        Fabric1170PlatformPlayer::new,
                        Fabric1170AltarACEntity::new,
                        Fabric1161PlatformInventory::new
                ),
                PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_17)
                        ? new Fabric1171PlatformServer() : new Fabric1140PlatformServer(),
                new Fabric1161MessageUtil(),
                new Fabric1140ConversionUtil()
        );
    }

    protected AltarACFabric1170LoaderPlugin(LazyHolder<CloudPlatformCommandArguments> commandArguments,
                                           FabricPlatformPlayerFactory playerFactory,
                                           AbstractFabricPlatformServer platformServer,
                                           IFabricMessageUtil fabricMessageUtil,
                                           IFabricConversionUtil fabricConversionUtil) {
        super(
                commandArguments,
                playerFactory,
                platformServer,
                fabricMessageUtil,
                fabricConversionUtil
        );
    }

    @Override
    public ServerVersion getNativeVersion() {
        return ServerVersion.V_1_17_1;
    }
}
