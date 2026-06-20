package ac.altarac.platform.fabric.mc1161;

import ac.altarac.platform.fabric.AbstractFabricPlatformServer;
import ac.altarac.platform.fabric.AltarACFabricIntermediaryLoaderPlugin;
import ac.altarac.platform.fabric.mc1161.entity.Fabric1161AltarACEntity;
import ac.altarac.platform.fabric.mc1161.player.Fabric1161PlatformInventory;
import ac.altarac.platform.fabric.mc1161.player.Fabric1161PlatformPlayer;
import ac.altarac.platform.fabric.mc1161.util.convert.Fabric1140ConversionUtil;
import ac.altarac.platform.fabric.mc1161.util.convert.Fabric1161MessageUtil;
import ac.altarac.platform.fabric.player.FabricPlatformPlayerFactory;
import ac.altarac.platform.fabric.utils.convert.IFabricConversionUtil;
import ac.altarac.platform.fabric.utils.message.IFabricMessageUtil;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

public class AltarACFabric1161LoaderPlugin extends AltarACFabricIntermediaryLoaderPlugin {

    public AltarACFabric1161LoaderPlugin() {
        this(
            new FabricPlatformPlayerFactory(
                Fabric1161PlatformPlayer::new,
                Fabric1161AltarACEntity::new,
                Fabric1161PlatformInventory::new
            ),
            new Fabric1140PlatformServer(),
            new Fabric1161MessageUtil(),
            new Fabric1140ConversionUtil()
        );
    }

    protected AltarACFabric1161LoaderPlugin(
            FabricPlatformPlayerFactory playerFactory,
            AbstractFabricPlatformServer platformServer,
            IFabricMessageUtil fabricMessageUtil,
            IFabricConversionUtil fabricConversionUtil
    ) {
        super(AltarACFabricIntermediaryLoaderPlugin::createCommandArguments,
            playerFactory,
            platformServer,
            fabricMessageUtil,
            fabricConversionUtil
        );
    }

    @Override
    public ServerVersion getNativeVersion() {
        return ServerVersion.V_1_16_1;
    }
}
