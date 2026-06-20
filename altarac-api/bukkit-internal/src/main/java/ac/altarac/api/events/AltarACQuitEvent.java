package ac.altarac.api.events;

import ac.altarac.api.AltarACUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Deprecated(since = "1.2.1.0", forRemoval = true)
public class AltarACQuitEvent extends Event implements AltarACUserEvent {

    private static final HandlerList handlers = new HandlerList();
    private final AltarACUser user;

    public AltarACQuitEvent(AltarACUser user) {
        super(true); // Async!
        this.user = user;
    }

    @Override
    public AltarACUser getUser() {
        return user;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
