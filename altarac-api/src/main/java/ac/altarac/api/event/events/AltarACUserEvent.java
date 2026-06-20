package ac.altarac.api.event.events;

import ac.altarac.api.AltarACUser;

public interface AltarACUserEvent {
    AltarACUser getUser();
    default AltarACUser getPlayer() {
        return getUser();
    }
}

