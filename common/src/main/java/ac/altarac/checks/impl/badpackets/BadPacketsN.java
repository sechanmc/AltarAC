package ac.altarac.checks.impl.badpackets;

import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.player.AltarACPlayer;

@CheckData(name = "BadPacketsN", stableKey = "AltarAC.badpackets.invalid_teleport", description = "Ignored or failed to accept a required server teleport", setback = 0)
public class BadPacketsN extends Check {
    public BadPacketsN(final AltarACPlayer player) {
        super(player);
    }
}
