package ac.altarac.internal.storage.checks;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Canonical {@code AltarAC.legacy.* → AltarAC.<category>.<descriptive>} rename map.
 * Consumed by:
 *
 * <ul>
 *   <li>{@link StableKeyMapping} when assigning stable_keys during V0→V1 migration.</li>
 *   <li>Each backend's schema migration step that runs an
 *       {@code UPDATE AltarAC_checks SET stable_key = ? WHERE stable_key = ?} pass
 *       to rewrite already-persisted rows on existing operator installs.</li>
 * </ul>
 *
 * <p>Order matters only for readability — every entry's old key is unique and
 * the migration applies them all idempotently. Entries omitted here keep their
 * {@code AltarAC.legacy.*} key forever (V0-only historical checks with no live V2
 * class — aim-fold/gold/hold, looka, clientbrand — stay legacy because there
 * is no source-of-truth class to rename).
 */
@ApiStatus.Internal
public final class LegacyKeyRenames {

    private LegacyKeyRenames() {}

    public static final Map<String, String> OLD_TO_NEW;

    static {
        Map<String, String> m = new LinkedHashMap<>();

        // BadPackets — letter-keyed checks where V2 and V3 diverged in semantics.
        m.put("AltarAC.legacy.badpacketsb", "AltarAC.badpackets.ignored_rotation");
        m.put("AltarAC.legacy.badpacketsc", "AltarAC.badpackets.wake_not_sleeping");
        m.put("AltarAC.legacy.badpacketsh", "AltarAC.badpackets.unexpected_sequence");
        m.put("AltarAC.legacy.badpacketsj", "AltarAC.badpackets.use_item_rotation_mismatch");
        m.put("AltarAC.legacy.badpacketsr", "AltarAC.badpackets.position_starvation");
        m.put("AltarAC.legacy.badpacketss", "AltarAC.badpackets.window_confirmation_not_accepted");
        m.put("AltarAC.legacy.badpacketst", "AltarAC.badpackets.invalid_interact_vector");
        m.put("AltarAC.legacy.badpacketsw", "AltarAC.badpackets.invalid_entity_target");
        m.put("AltarAC.legacy.badpacketsx", "AltarAC.badpackets.extra_input_actions");
        m.put("AltarAC.legacy.badpacketsz", "AltarAC.badpackets.duplicate_player_input");

        // Single-check categories.
        m.put("AltarAC.legacy.chatc", "AltarAC.chat.moving_while_chatting");
        m.put("AltarAC.legacy.exploita", "AltarAC.exploit.anvil_name_length");
        m.put("AltarAC.legacy.groundspoof", "AltarAC.groundspoof.fake");
        m.put("AltarAC.legacy.timerlimit", "AltarAC.timer.limit");

        // Elytra — every check fires when the player STARTS gliding under some
        // disallowed condition; the category implies the verb, the suffix is
        // just the condition.
        m.put("AltarAC.legacy.elytraa", "AltarAC.elytra.already_gliding");
        m.put("AltarAC.legacy.elytrab", "AltarAC.elytra.no_jump");
        m.put("AltarAC.legacy.elytrac", "AltarAC.elytra.too_frequent");
        m.put("AltarAC.legacy.elytrad", "AltarAC.elytra.no_elytra");
        m.put("AltarAC.legacy.elytrae", "AltarAC.elytra.flying");
        m.put("AltarAC.legacy.elytraf", "AltarAC.elytra.grounded");
        m.put("AltarAC.legacy.elytrag", "AltarAC.elytra.levitation");
        m.put("AltarAC.legacy.elytrah", "AltarAC.elytra.vehicle");
        m.put("AltarAC.legacy.elytrai", "AltarAC.elytra.water");

        // MultiActions — two simultaneous actions, named <verb>_while_<context>.
        m.put("AltarAC.legacy.multiactionsa", "AltarAC.multiactions.attack_while_using");
        m.put("AltarAC.legacy.multiactionsb", "AltarAC.multiactions.break_while_using");
        m.put("AltarAC.legacy.multiactionsc", "AltarAC.multiactions.inventory_click_while_moving");
        m.put("AltarAC.legacy.multiactionsd", "AltarAC.multiactions.inventory_close_while_moving");
        m.put("AltarAC.legacy.multiactionse", "AltarAC.multiactions.swing_while_using");
        m.put("AltarAC.legacy.multiactionsf", "AltarAC.multiactions.block_and_entity_interact");
        m.put("AltarAC.legacy.multiactionsg", "AltarAC.multiactions.action_while_rowing");

        // MultiInteract.
        m.put("AltarAC.legacy.multiinteracta", "AltarAC.multiinteract.multiple_targets");
        m.put("AltarAC.legacy.multiinteractb", "AltarAC.multiinteract.interact_at_position_changed");

        // PacketOrder — every check is "X happened in the wrong order"; the
        // <thing>_order suffix matches the colleague's naming style.
        m.put("AltarAC.legacy.packetordera", "AltarAC.packetorder.window_click_order");
        m.put("AltarAC.legacy.packetorderb", "AltarAC.packetorder.noswing");
        m.put("AltarAC.legacy.packetorderc", "AltarAC.packetorder.interact_order");
        m.put("AltarAC.legacy.packetorderd", "AltarAC.packetorder.interact_hand_order");
        m.put("AltarAC.legacy.packetordere", "AltarAC.packetorder.slot_order");
        m.put("AltarAC.legacy.packetorderf", "AltarAC.packetorder.input_tick_to_sneak_sprint_order");
        m.put("AltarAC.legacy.packetorderg", "AltarAC.packetorder.hotbar_inventory_manage_order");
        m.put("AltarAC.legacy.packetorderh", "AltarAC.packetorder.sneak_sprint_order");
        m.put("AltarAC.legacy.packetorderi", "AltarAC.packetorder.input_tick_order");
        m.put("AltarAC.legacy.packetorderj", "AltarAC.packetorder.attack_interact_use_order");
        m.put("AltarAC.legacy.packetorderk", "AltarAC.packetorder.inventory_open_order");
        m.put("AltarAC.legacy.packetorderl", "AltarAC.packetorder.drop_item_order");
        m.put("AltarAC.legacy.packetorderm", "AltarAC.packetorder.interact_use_order");
        m.put("AltarAC.legacy.packetordern", "AltarAC.packetorder.place_use_order");
        m.put("AltarAC.legacy.packetordero", "AltarAC.packetorder.tick_end_order");
        m.put("AltarAC.legacy.packetorderp", "AltarAC.packetorder.transaction_response_order");

        // Sprint — terse condition names; category implies "started sprinting".
        m.put("AltarAC.legacy.sprinta", "AltarAC.sprint.hunger");
        m.put("AltarAC.legacy.sprintb", "AltarAC.sprint.sneaking");
        m.put("AltarAC.legacy.sprintc", "AltarAC.sprint.using_item");
        m.put("AltarAC.legacy.sprintd", "AltarAC.sprint.blindness");
        m.put("AltarAC.legacy.sprinte", "AltarAC.sprint.wall");
        m.put("AltarAC.legacy.sprintf", "AltarAC.sprint.gliding");
        m.put("AltarAC.legacy.sprintg", "AltarAC.sprint.water");

        // Vehicle.
        m.put("AltarAC.legacy.vehiclea", "AltarAC.vehicle.impossible_input");
        m.put("AltarAC.legacy.vehicleb", "AltarAC.vehicle.spoofed_vehicle");
        m.put("AltarAC.legacy.vehiclec", "AltarAC.vehicle.vehicle_control");
        m.put("AltarAC.legacy.vehicled", "AltarAC.vehicle.spoofed_jump");
        m.put("AltarAC.legacy.vehiclee", "AltarAC.vehicle.spoofed_boat");
        m.put("AltarAC.legacy.vehiclef", "AltarAC.vehicle.boat_input_mismatch");

        OLD_TO_NEW = Collections.unmodifiableMap(m);
    }
}
