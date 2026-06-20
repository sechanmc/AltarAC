package ac.altarac.checks.impl.badpackets;

import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.player.AltarACPlayer;

@CheckData(name = "BadPacketsW", stableKey = "AltarAC.badpackets.invalid_entity_target", description = "Interacted with non-existent entity", experimental = true)
public class BadPacketsW extends Check {
    public BadPacketsW(AltarACPlayer player) {
        super(player);
    }
}
