package ac.altarac.checks.impl.combat;

import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.player.AltarACPlayer;

@CheckData(name = "Hitboxes", stableKey = "AltarAC.combat.hitboxes", description = "Tried to hit an entity outside its valid hitbox", setback = 10)
public class Hitboxes extends Check {
    public Hitboxes(AltarACPlayer player) {
        super(player);
    }
}
