package io.github.tacticalaxis.customguishop.manager;

import io.github.tacticalaxis.customguishop.util.Strings;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ReputationManager {

    final public static int MAX_LEVEL = 30;

    // get a player's reputation
    public static int getReputation(Player player) {
        return ConfigurationManager.getInstance().getPlayerConfiguration().getConfigurationSection(Strings.CONFIG_REPUTATION_SECTION).getInt(player.getUniqueId().toString());
    }

    // get a player's level
    public static int getReputationLevel(Player player) {
        int current = getReputation(player);
        for (int level = 0; level <= MAX_LEVEL; level++) {
            if (Math.pow(2, level) > current) {
                return level;
            }
        }
        return MAX_LEVEL;
    }

    // add reputation to a player
    public static void addReputation(Player player, int amount, String reason, String target) {
        ConfigurationSection rep = ConfigurationManager.getInstance().getMainConfiguration().getConfigurationSection(Strings.CONFIG_REPUTATION_SECTION);
        int old = getReputation(player);
        int required = getNextLevel(player);
        if (amount >= required) {
            if (getReputationLevel(player) < getNextLevelOverall(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', rep.getString(Strings.CONFIG_REPUTATION_LEVEL_UPGRADE).replace("%level%", String.valueOf(getReputationLevel(player) + 1))));
            }
        }
        ConfigurationManager.getInstance().getPlayerConfiguration().getConfigurationSection(Strings.CONFIG_REPUTATION_SECTION).set(player.getUniqueId().toString(), old + amount);
        ConfigurationManager.getInstance().savePlayerConfiguration();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', rep.getString(Strings.CONFIG_REPUTATION_ON_ACTION_MESSAGE).replace("%action%", reason).replace("%amount%", String.valueOf(amount)).replace("%target%", target)));
    }

    // get the amount of RP required for the next level
    public static int getNextLevel(Player player) {
        int current = getReputation(player);
        for (int level = 0; level <= MAX_LEVEL; level++) {
            if (Math.pow(2, level) > current) {
                return (int) (Math.pow(2, level) - current);
            }
        }
        return 0;
    }

    public static int getNextLevelOverall(Player player) {
        int current = getReputation(player);
        for (int level = 0; level <= MAX_LEVEL; level++) {
            if (Math.pow(2, level) > current) {
                return level + 1;
            }
        }
        return 0;
    }
}