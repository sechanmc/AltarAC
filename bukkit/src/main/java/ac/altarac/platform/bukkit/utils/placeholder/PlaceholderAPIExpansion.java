package ac.altarac.platform.bukkit.utils.placeholder;

import ac.altarac.AltarACAPI;
import ac.altarac.api.AltarACUser;
import ac.altarac.player.AltarACPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "AltarAC";
    }

    public @NotNull String getAuthor() {
        return String.join(", ", AltarACAPI.INSTANCE.getPlugin().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return AltarACAPI.INSTANCE.getExternalAPI().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        Set<String> staticReplacements = AltarACAPI.INSTANCE.getExternalAPI().getStaticReplacements().keySet();
        Set<String> variableReplacements = AltarACAPI.INSTANCE.getExternalAPI().getVariableReplacements().keySet();
        ArrayList<String> placeholders = new ArrayList<>(staticReplacements.size() + variableReplacements.size());
        for (String s : staticReplacements) {
            placeholders.add(s.equals("%AltarAC_version%") ? s : "%AltarAC_" + s.replace("%", "") + "%");
        }
        for (String s : variableReplacements) {
            placeholders.add(s.equals("%player%") ? "%AltarAC_player%" : "%AltarAC_player_" + s.replace("%", "") + "%");
        }
        return placeholders;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        for (Map.Entry<String, String> entry : AltarACAPI.INSTANCE.getExternalAPI().getStaticReplacements().entrySet()) {
            String key = entry.getKey().equals("%AltarAC_version%")
                    ? "version"
                    : entry.getKey().replace("%", "");
            if (params.equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }

        if (offlinePlayer instanceof Player player) {
            AltarACPlayer AltarACPlayer = AltarACAPI.INSTANCE.getPlayerDataManager().getPlayer(player.getUniqueId());
            if (AltarACPlayer == null) return null;

            for (Map.Entry<String, Function<AltarACUser, String>> entry : AltarACAPI.INSTANCE.getExternalAPI().getVariableReplacements().entrySet()) {
                String key = entry.getKey().equals("%player%")
                        ? "player"
                        : "player_" + entry.getKey().replace("%", "");
                if (params.equalsIgnoreCase(key)) {
                    return entry.getValue().apply(AltarACPlayer);
                }
            }
        }

        return null;
    }
}
