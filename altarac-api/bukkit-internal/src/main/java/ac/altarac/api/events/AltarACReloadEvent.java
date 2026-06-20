package ac.altarac.api.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Deprecated(since = "1.2.1.0", forRemoval = true)
public class AltarACReloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final boolean success;

    public AltarACReloadEvent(boolean success) {
        super(true); // Async!
        this.success = success;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
