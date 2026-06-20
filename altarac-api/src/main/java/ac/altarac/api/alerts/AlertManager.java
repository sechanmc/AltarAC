package ac.altarac.api.alerts;

import ac.altarac.api.AltarACUser;
import lombok.NonNull;
import org.bukkit.entity.Player;

public interface AlertManager {

    /**
     * Checks if the player has alerts enabled.
     *
     * @param player The AltarACUser to check
     * @return true if the player has alerts enabled, false otherwise
     * @throws NullPointerException if player is null
     */
    boolean hasAlertsEnabled(@NonNull AltarACUser player);

    /**
     * Toggles alerts for the player.
     * Sends a message to the player indicating the new state.
     *
     * @param player The AltarACUser to toggle alerts for
     * @return true if alerts are now enabled, false if alerts are now disabled
     * @throws NullPointerException if player is null
     */
    default boolean toggleAlerts(@NonNull AltarACUser player) {
        return toggleAlerts(player, false);
    }

    /**
     * Toggles alerts for the player silently or with a message.
     *
     * @param player The AltarACUser to toggle alerts for
     * @param silent true to suppress any messages to the player, false to notify
     * @return true if alerts are now enabled, false if alerts are now disabled
     * @throws NullPointerException if player is null
     */
    default boolean toggleAlerts(@NonNull AltarACUser player, boolean silent) {
        boolean newState = !hasAlertsEnabled(player);
        setAlertsEnabled(player, newState, silent);
        return newState;
    }

    /**
     * Sets the alert state for the player.
     * Sends a message to the player indicating the new state.
     *
     * @param player The AltarACUser to set alerts for
     * @param enabled true to enable alerts, false to disable
     * @throws NullPointerException if player is null
     */
    default void setAlertsEnabled(@NonNull AltarACUser player, boolean enabled) {
        setAlertsEnabled(player, enabled, false);
    }

    /**
     * Sets the alert state for the player silently or with a message.
     *
     * @param player The AltarACUser to set alerts for
     * @param enabled true to enable alerts, false to disable
     * @param silent true to suppress any messages to the player, false to notify
     * @throws NullPointerException if player is null
     */
    void setAlertsEnabled(@NonNull AltarACUser player, boolean enabled, boolean silent);

    // ------------------- VERBOSE -------------------

    /**
     * Checks if the player has verbose enabled.
     *
     * @param player The AltarACUser to check
     * @return true if the player has verbose enabled, false otherwise
     * @throws NullPointerException if player is null
     */
    boolean hasVerboseEnabled(@NonNull AltarACUser player);

    /**
     * Toggles verbose for the player.
     * Sends a message to the player indicating the new state.
     *
     * @param player The AltarACUser to toggle verbose for
     * @return true if verbose is now enabled, false if verbose is now disabled
     * @throws NullPointerException if player is null
     */
    default boolean toggleVerbose(@NonNull AltarACUser player) {
        return toggleVerbose(player, false);
    }

    /**
     * Toggles verbose for the player silently or with a message.
     *
     * @param player The AltarACUser to toggle verbose for
     * @param silent true to suppress any messages to the player, false to notify
     * @return true if verbose is now enabled, false if verbose is now disabled
     * @throws NullPointerException if player is null
     */
    default boolean toggleVerbose(@NonNull AltarACUser player, boolean silent) {
        boolean newState = !hasVerboseEnabled(player);
        setVerboseEnabled(player, newState, silent);
        return newState;
    }

    /**
     * Sets the verbose state for the player.
     * Sends a message to the player indicating the new state.
     *
     * @param player The AltarACUser to set verbose for
     * @param enabled true to enable verbose, false to disable
     * @throws NullPointerException if player is null
     */
    default void setVerboseEnabled(@NonNull AltarACUser player, boolean enabled) {
        setVerboseEnabled(player, enabled, false);
    }

    /**
     * Sets the verbose state for the player silently or with a message.
     *
     * @param player The AltarACUser to set verbose for
     * @param enabled true to enable verbose, false to disable
     * @param silent true to suppress any messages to the player, false to notify
     * @throws NullPointerException if player is null
     */
    void setVerboseEnabled(@NonNull AltarACUser player, boolean enabled, boolean silent);

    // ------------------- BRANDS -------------------

    /**
     * Checks if the player has brand notifications enabled.
     *
     * @param player The AltarACUser to check
     * @return true if the player has brand notifications enabled and has the "AltarAC.brand" permission,
     *         false otherwise
     * @throws NullPointerException if player is null
     */
    boolean hasBrandsEnabled(@NonNull AltarACUser player);

    /**
     * Toggles brand notifications for the player.
     * Sends a message to the player indicating the new state.
     *
     * @param player The AltarACUser to toggle brand notifications for
     * @return true if brand notifications are now enabled, false if brand notifications are now disabled
     * @throws NullPointerException if player is null
     */
    default boolean toggleBrands(@NonNull AltarACUser player) {
        return toggleBrands(player, false);
    }

    /**
     * Toggles brand notifications for the player silently or with a message.
     *
     * @param player The AltarACUser to toggle brand notifications for
     * @param silent true to suppress any messages to the player, false to notify
     * @return true if brand notifications are now enabled, false if brand notifications are now disabled
     * @throws NullPointerException if player is null
     */
    default boolean toggleBrands(@NonNull AltarACUser player, boolean silent) {
        boolean newState = !hasBrandsEnabled(player);
        setBrandsEnabled(player, newState, silent);
        return newState;
    }

    /**
     * Sets the brand notification state for the player.
     * Sends a message to the player indicating the new state.
     *
     * @param player The AltarACUser to set brand notifications for
     * @param enabled true to enable brand notifications, false to disable
     * @throws NullPointerException if player is null
     */
    default void setBrandsEnabled(@NonNull AltarACUser player, boolean enabled) {
        setBrandsEnabled(player, enabled, false);
    }

    /**
     * Sets the brand notification state for the player silently or with a message.
     *
     * @param player The AltarACUser to set brand notifications for
     * @param enabled true to enable brand notifications, false to disable
     * @param silent true to suppress any messages to the player, false to notify
     * @throws NullPointerException if player is null
     */
    void setBrandsEnabled(@NonNull AltarACUser player, boolean enabled, boolean silent);

    /**
     * Checks if the player has alerts enabled.
     * @param player
     * @return boolean
     */
    @Deprecated boolean hasAlertsEnabled(Player player);

    /**
     * Toggles alerts for the player.
     * @param player
     */
    @Deprecated void toggleAlerts(Player player);
    /**
     * Checks if the player has verbose enabled.
     * @param player
     * @return boolean
     */
    @Deprecated
    boolean hasVerboseEnabled(Player player);

    /**
     * Toggles verbose for the player.
     * @param player
     */
    @Deprecated void toggleVerbose(Player player);
}