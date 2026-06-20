package ac.altarac.checks.impl.misc;

import ac.altarac.checks.Check;
import ac.altarac.checks.CheckData;
import ac.altarac.player.AltarACPlayer;

@CheckData(name = "TransactionOrder", stableKey = "AltarAC.ping.invalid_transaction_order", description = "Sent transaction or ping responses in an invalid order")
public class TransactionOrder extends Check {
    public TransactionOrder(AltarACPlayer player) {
        super(player);
    }
}
