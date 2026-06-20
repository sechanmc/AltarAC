package ac.altarac.api.events;

import ac.altarac.api.AltarACUser;

@Deprecated(since = "1.2.1.0", forRemoval = true)
public interface AltarACUserEvent {

    AltarACUser getUser();

    default AltarACUser getPlayer() {
        return getUser();
    }

}
