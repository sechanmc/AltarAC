package ac.altarac.internal.storage.checks;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Bridges legacy {@code AltarAC_history_check_names} display strings to the v1
 * schema's {@code stable_key} column.
 * <p>
 * Two consumers:
 * <ul>
 *   <li>v0 → v1 migration: {@code LegacyMigrator} resolves every distinct
 *       {@code check_name_string} through this map so historical violations
 *       land on the new schema with a meaningful stable_key.</li>
 *   <li>Live-write fallback: {@code LiveWriteHooks} consults this map when a
 *       Check hasn't declared a {@code stableKey} on its {@code @CheckData}
 *       / {@code CheckInfo}.</li>
 * </ul>
 * <p>
 * Unknown names hit the {@link #legacyFallback} path so migration always
 * completes; operators can rename the fallback keys later through the check
 * registry if they want.
 */
@ApiStatus.Internal
public final class StableKeyMapping {

    private static final Map<String, String> MAPPINGS = buildMappings();

    private StableKeyMapping() {}

    public static Optional<String> stableKeyFor(String legacyDisplayName) {
        if (legacyDisplayName == null) return Optional.empty();
        return Optional.ofNullable(MAPPINGS.get(legacyDisplayName.toLowerCase(Locale.ROOT)));
    }

    public static String legacyFallback(String legacyDisplayName) {
        return "AltarAC.legacy." + legacyDisplayName.toLowerCase(Locale.ROOT);
    }

    private static Map<String, String> buildMappings() {
        // Keys: lowercased legacy display names (the {@code check_name_string}
        // column in the v0 schema, plus any {@code Check.getCheckName()} a
        // live writer might emit).
        // Values: stable_keys declared via @CheckData.stableKey on the
        // matching Check class. Entries for retired check classes that have
        // no live source-of-truth keep their {@code AltarAC.legacy.*} key so
        // historical violations stay addressable.
        Map<String, String> m = new HashMap<>(160);

        // ---------- badpackets/ ----------
        m.put("badpacketsa", "AltarAC.badpackets.duplicate_slot");
        m.put("badpacketsd", "AltarAC.badpackets.invalid_pitch");
        m.put("badpacketse", "AltarAC.badpackets.invalid_position");
        m.put("badpacketsf", "AltarAC.badpackets.duplicate_sprint");
        m.put("badpacketsg", "AltarAC.badpackets.duplicate_sneak");
        m.put("badpacketsi", "AltarAC.badpackets.spoofed_abilities");
        m.put("badpacketsk", "AltarAC.badpackets.invalid_spectate");
        m.put("badpacketsl", "AltarAC.badpackets.invalid_dig");
        m.put("badpacketsm", "AltarAC.badpackets.respawn_alive");
        m.put("badpacketsn", "AltarAC.badpackets.invalid_teleport");
        m.put("badpacketso", "AltarAC.badpackets.invalid_keepalive");
        m.put("badpacketsp", "AltarAC.badpackets.invalid_click");
        m.put("badpacketsq", "AltarAC.badpackets.invalid_horse_jump");
        m.put("badpacketsu", "AltarAC.badpackets.invalid_block_placement");
        m.put("badpacketsv", "AltarAC.badpackets.slow_move");
        m.put("badpacketsy", "AltarAC.badpackets.oob_slot");
        m.put("badpacketsb", "AltarAC.badpackets.ignored_rotation");
        m.put("badpacketsc", "AltarAC.badpackets.wake_not_sleeping");
        m.put("badpacketsh", "AltarAC.badpackets.unexpected_sequence");
        m.put("badpacketsj", "AltarAC.badpackets.use_item_rotation_mismatch");
        m.put("badpacketsr", "AltarAC.badpackets.position_starvation");
        m.put("badpacketss", "AltarAC.badpackets.window_confirmation_not_accepted");
        m.put("badpacketst", "AltarAC.badpackets.invalid_interact_vector");
        m.put("badpacketsw", "AltarAC.badpackets.invalid_entity_target");
        m.put("badpacketsx", "AltarAC.badpackets.extra_input_actions");
        m.put("badpacketsz", "AltarAC.badpackets.duplicate_player_input");
        m.put("selfinteract", "AltarAC.badpackets.self_hit");

        // ---------- crash/ ----------
        m.put("crasha", "AltarAC.crash.large_position");
        m.put("crashb", "AltarAC.crash.creative_while_not_creative");
        m.put("crashc", "AltarAC.crash.nan_position");
        m.put("crashd", "AltarAC.crash.lectern");
        m.put("crashe", "AltarAC.crash.low_view_distance");
        m.put("crashf", "AltarAC.crash.button_crash");
        m.put("crashg", "AltarAC.crash.negative_sequence");
        m.put("crashh", "AltarAC.crash.invalid_tab_complete");
        m.put("crashi", "AltarAC.crash.invalid_bundle_slot");

        // ---------- combat/ ----------
        m.put("hitboxes", "AltarAC.combat.hitboxes");
        m.put("reach", "AltarAC.combat.reach");

        // ---------- aim/ ----------
        m.put("aimduplicatelook", "AltarAC.aim.duplicate_look");
        m.put("aimmodulo360", "AltarAC.aim.modulo_360");
        m.put("aimfold", "AltarAC.legacy.aimfold");
        m.put("aimgold", "AltarAC.legacy.aimgold");
        m.put("aimhold", "AltarAC.legacy.aimhold");

        // ---------- breaking/ ----------
        m.put("airliquidbreak", "AltarAC.breaking.air_liquid_break");
        m.put("farbreak", "AltarAC.breaking.far_break");
        m.put("fastbreak", "AltarAC.breaking.fast_break");
        m.put("invalidbreak", "AltarAC.breaking.invalid_break");
        m.put("multibreak", "AltarAC.breaking.multi_break");
        m.put("noswingbreak", "AltarAC.breaking.no_swing_break");
        m.put("positionbreaka", "AltarAC.breaking.position_break_a");
        m.put("positionbreakb", "AltarAC.breaking.position_break_b");
        m.put("rotationbreak", "AltarAC.breaking.rotation_break");
        m.put("wrongbreak", "AltarAC.breaking.wrong_break");

        // ---------- scaffolding/ ----------
        m.put("airliquidplace", "AltarAC.scaffolding.air_liquid_place");
        m.put("duplicaterotplace", "AltarAC.scaffolding.duplicate_rot_place");
        m.put("fabricatedplace", "AltarAC.scaffolding.fabricated_place");
        m.put("farplace", "AltarAC.scaffolding.far_place");
        m.put("invalidplacea", "AltarAC.scaffolding.invalid_place_a");
        m.put("invalidplaceb", "AltarAC.scaffolding.invalid_place_b");
        m.put("multiplace", "AltarAC.scaffolding.multi_place");
        m.put("positionplace", "AltarAC.scaffolding.position_place");
        m.put("rotationplace", "AltarAC.scaffolding.rotation_place");

        // ---------- chat/ ----------
        m.put("chatc", "AltarAC.chat.moving_while_chatting");

        // ---------- exploit/ ----------
        m.put("chata", "AltarAC.exploit.blank_tab_complete");
        m.put("chatb", "AltarAC.exploit.spigot_antispam_bypass");
        m.put("chatd", "AltarAC.exploit.chat_while_hidden");
        m.put("exploita", "AltarAC.exploit.anvil_name_length");
        m.put("exploitb", "AltarAC.exploit.invalid_book_edit");
        m.put("exploitc", "AltarAC.legacy.exploitc");

        // ---------- prediction/ ----------
        m.put("phase", "AltarAC.prediction.phase");

        // ---------- movement/ ----------
        m.put("noslow", "AltarAC.movement.noslow");

        // ---------- groundspoof/ ----------
        m.put("groundspoof", "AltarAC.groundspoof.fake");
        m.put("nofall", "AltarAC.groundspoof.no_fall");

        // ---------- post/ ----------
        m.put("post", "AltarAC.post.invalid_order");

        // ---------- ping/ ----------
        m.put("transactionorder", "AltarAC.ping.invalid_transaction_order");

        // ---------- baritone/ ----------
        m.put("baritone", "AltarAC.baritone.baritone");

        // ---------- timer/ ----------
        m.put("negativetimer", "AltarAC.timer.negative");
        m.put("ticktimer", "AltarAC.timer.tick");
        m.put("timer", "AltarAC.timer.timer");
        m.put("timerlimit", "AltarAC.timer.limit");
        m.put("vehicletimer", "AltarAC.timer.vehicle");

        // ---------- elytra/ ----------
        m.put("elytraa", "AltarAC.elytra.already_gliding");
        m.put("elytrab", "AltarAC.elytra.no_jump");
        m.put("elytrac", "AltarAC.elytra.too_frequent");
        m.put("elytrad", "AltarAC.elytra.no_elytra");
        m.put("elytrae", "AltarAC.elytra.flying");
        m.put("elytraf", "AltarAC.elytra.grounded");
        m.put("elytrag", "AltarAC.elytra.levitation");
        m.put("elytrah", "AltarAC.elytra.vehicle");
        m.put("elytrai", "AltarAC.elytra.water");

        // ---------- sprint/ ----------
        m.put("sprinta", "AltarAC.sprint.hunger");
        m.put("sprintb", "AltarAC.sprint.sneaking");
        m.put("sprintc", "AltarAC.sprint.using_item");
        m.put("sprintd", "AltarAC.sprint.blindness");
        m.put("sprinte", "AltarAC.sprint.wall");
        m.put("sprintf", "AltarAC.sprint.gliding");
        m.put("sprintg", "AltarAC.sprint.water");

        // ---------- vehicle/ ----------
        m.put("vehiclea", "AltarAC.vehicle.impossible_input");
        m.put("vehicleb", "AltarAC.vehicle.spoofed_vehicle");
        m.put("vehiclec", "AltarAC.vehicle.vehicle_control");
        m.put("vehicled", "AltarAC.vehicle.spoofed_jump");
        m.put("vehiclee", "AltarAC.vehicle.spoofed_boat");
        m.put("vehiclef", "AltarAC.vehicle.boat_input_mismatch");

        // ---------- multiactions/ ----------
        m.put("multiactionsa", "AltarAC.multiactions.attack_while_using");
        m.put("multiactionsb", "AltarAC.multiactions.break_while_using");
        m.put("multiactionsc", "AltarAC.multiactions.inventory_click_while_moving");
        m.put("multiactionsd", "AltarAC.multiactions.inventory_close_while_moving");
        m.put("multiactionse", "AltarAC.multiactions.swing_while_using");
        m.put("multiactionsf", "AltarAC.multiactions.block_and_entity_interact");
        m.put("multiactionsg", "AltarAC.multiactions.action_while_rowing");

        // ---------- multiinteract/ ----------
        m.put("multiinteracta", "AltarAC.multiinteract.multiple_targets");
        m.put("multiinteractb", "AltarAC.multiinteract.interact_at_position_changed");

        // ---------- packetorder/ ----------
        m.put("packetordera", "AltarAC.packetorder.window_click_order");
        m.put("packetorderb", "AltarAC.packetorder.noswing");
        m.put("packetorderc", "AltarAC.packetorder.interact_order");
        m.put("packetorderd", "AltarAC.packetorder.interact_hand_order");
        m.put("packetordere", "AltarAC.packetorder.slot_order");
        m.put("packetorderf", "AltarAC.packetorder.input_tick_to_sneak_sprint_order");
        m.put("packetorderg", "AltarAC.packetorder.hotbar_inventory_manage_order");
        m.put("packetorderh", "AltarAC.packetorder.sneak_sprint_order");
        m.put("packetorderi", "AltarAC.packetorder.input_tick_order");
        m.put("packetorderj", "AltarAC.packetorder.attack_interact_use_order");
        m.put("packetorderk", "AltarAC.packetorder.inventory_open_order");
        m.put("packetorderl", "AltarAC.packetorder.drop_item_order");
        m.put("packetorderm", "AltarAC.packetorder.interact_use_order");
        m.put("packetordern", "AltarAC.packetorder.place_use_order");
        m.put("packetordero", "AltarAC.packetorder.tick_end_order");
        m.put("packetorderp", "AltarAC.packetorder.transaction_response_order");

        // ---------- misc-legacy/ ----------
        m.put("looka", "AltarAC.legacy.looka");
        m.put("clientbrand", "AltarAC.legacy.clientbrand");
        m.put("inventorya", "AltarAC.legacy.inventorya");
        m.put("inventoryb", "AltarAC.legacy.inventoryb");
        m.put("inventoryc", "AltarAC.legacy.inventoryc");
        m.put("inventoryd", "AltarAC.legacy.inventoryd");
        m.put("inventorye", "AltarAC.legacy.inventorye");
        m.put("inventoryf", "AltarAC.legacy.inventoryf");
        m.put("inventoryg", "AltarAC.legacy.inventoryg");

        return Map.copyOf(m);
    }
}

