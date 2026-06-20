package ac.altarac.checks.debug;

import ac.altarac.checks.Check;
import ac.altarac.player.AltarACPlayer;

public abstract class AbstractDebugHandler extends Check {
    public AbstractDebugHandler(AltarACPlayer player) {
        super(player);
    }

    public abstract void toggleListener(AltarACPlayer player);

    public abstract boolean toggleConsoleOutput();
}
