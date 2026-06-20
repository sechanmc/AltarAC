package ac.altarac.platform.api.player;

import ac.altarac.api.AltarACIdentity;

public interface OfflinePlatformPlayer extends AltarACIdentity {

    boolean isOnline();

    String getName();
}
