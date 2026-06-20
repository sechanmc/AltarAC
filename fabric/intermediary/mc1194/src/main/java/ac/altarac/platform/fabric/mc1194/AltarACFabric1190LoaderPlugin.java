package ac.altarac.platform.fabric.mc1194;

import ac.altarac.platform.fabric.AbstractFabricPlatformServer;
import ac.altarac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.altarac.platform.fabric.AltarACFabricIntermediaryLoaderPlugin;
import ac.altarac.platform.fabric.mc1171.AltarACFabric1170LoaderPlugin;
import ac.altarac.platform.fabric.mc1171.player.Fabric1170PlatformPlayer;
import ac.altarac.platform.fabric.mc1194.convert.Fabric1190MessageUtil;
import ac.altarac.platform.fabric.mc1194.entity.Fabric1194AltarACEntity;
import ac.altarac.platform.fabric.mc1194.player.Fabric1193PlatformInventory;
import ac.altarac.platform.fabric.mc1161.player.Fabric1161PlatformInventory;
import ac.altarac.platform.fabric.mc1161.util.convert.Fabric1140ConversionUtil;
import ac.altarac.platform.fabric.player.FabricPlatformPlayerFactory;
import ac.altarac.platform.fabric.utils.convert.IFabricConversionUtil;
import ac.altarac.platform.fabric.utils.message.IFabricMessageUtil;
import ac.altarac.utils.lazy.LazyHolder;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;


public class AltarACFabric1190LoaderPlugin extends AltarACFabric1170LoaderPlugin {

    public AltarACFabric1190LoaderPlugin() {
        this(
            AltarACFabricIntermediaryLoaderPlugin::createCommandArguments,
            new FabricPlatformPlayerFactory(
                    Fabric1170PlatformPlayer::new,
                    Fabric1194AltarACEntity::new,
                    PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_19_2)
                            ? Fabric1193PlatformInventory::new : Fabric1161PlatformInventory::new
            ),
            new Fabric1190PlatformServer(),
            new Fabric1190MessageUtil(),
            new Fabric1140ConversionUtil()
        );
    }

    protected AltarACFabric1190LoaderPlugin(
            LazyHolder<CloudPlatformCommandArguments> commandArguments,
            FabricPlatformPlayerFactory platformPlayerFactory,
            AbstractFabricPlatformServer platformServer,
            IFabricMessageUtil fabricMessageUtil,
            IFabricConversionUtil fabricConversionUtil) {
        super(commandArguments, platformPlayerFactory, platformServer, fabricMessageUtil, fabricConversionUtil);
    }

    @Override
    public ServerVersion getNativeVersion() {
        return ServerVersion.V_1_19_4;
    }
}
