package ac.altarac.api.events;

import ac.altarac.api.AbstractCheck;
import ac.altarac.api.AltarACUser;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@Deprecated(since = "1.2.1.0", forRemoval = true)
public class CommandExecuteEvent extends FlagEvent {

    private static final HandlerList handlers = new HandlerList();
    @Getter private final String command;

    public CommandExecuteEvent(AltarACUser player, AbstractCheck check, String verbose, String command) {
        super(player, check, verbose); // Async!
        this.command = command;
    }

    public CommandExecuteEvent(AltarACUser player, AbstractCheck check, Supplier<String> verbose, String command) {
        super(player, check, verbose); // Async!
        this.command = command;
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
