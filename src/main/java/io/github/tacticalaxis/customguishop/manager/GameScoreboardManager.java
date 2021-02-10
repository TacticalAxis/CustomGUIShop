package io.github.tacticalaxis.customguishop.manager;

import io.github.tacticalaxis.customguishop.CustomGUIShop;
import io.github.tacticalaxis.customguishop.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

@SuppressWarnings("ALL")
public class GameScoreboardManager {

    // add player to scoreboard group
    private void addPlayerToGroup(Scoreboard board, Player player) {
        String playername = player.getName();
        board.registerNewTeam(playername);
        board.getTeam(playername).addPlayer(player);
    }

    // set the scoreboard for a player (for reals)
    public void setScoreboard(final Player player) {
        ConfigurationSection scoreboardSettings = ConfigurationManager.getInstance().getMainConfiguration().getConfigurationSection(Strings.CONFIG_SCOREBOARD_SECTION);

        ScoreboardManager scoreboardManager = CustomGUIShop.getInstance().getServer().getScoreboardManager();
        final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        addPlayerToGroup(scoreboard, player);
        final Objective objective = scoreboard.registerNewObjective("sb", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        final Objective objective2 = scoreboard.registerNewObjective("sb2", "dummy");
        objective2.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        // SERVER
        objective.getScore(ChatColor.translateAlternateColorCodes('&', scoreboardSettings.getString(Strings.CONFIG_SCOREBOARD_REPUTATION_TITLE))).setScore(15);
        final Team reputation = scoreboard.registerNewTeam("reputation");
        reputation.addPlayer(Bukkit.getOfflinePlayer(" "));
        objective.getScore(" ").setScore(14);
        objective.getScore("               ").setScore(13);

        // COINS
        objective.getScore(ChatColor.translateAlternateColorCodes('&', scoreboardSettings.getString(Strings.CONFIG_SCOREBOARD_LEVEL_TITLE))).setScore(12);
        final Team level = scoreboard.registerNewTeam("level");
        level.addPlayer(Bukkit.getOfflinePlayer("  "));
        objective.getScore("  ").setScore(11);
        objective.getScore("              ").setScore(10);

        // PIXELITES
        objective.getScore(ChatColor.translateAlternateColorCodes('&', scoreboardSettings.getString(Strings.CONFIG_SCOREBOARD_NEXTLEVEL_TITLE))).setScore(9);
        final Team nextLevel = scoreboard.registerNewTeam("nextLevel");
        nextLevel.addPlayer(Bukkit.getOfflinePlayer("   "));
        objective.getScore("   ").setScore(8);
        objective.getScore("            ").setScore(7);

        // RANK
        objective.getScore(ChatColor.translateAlternateColorCodes('&', scoreboardSettings.getString(Strings.CONFIG_SCOREBOARD_BALANCE_TITLE))).setScore(6);
        final Team balance = scoreboard.registerNewTeam("balance");
        balance.addPlayer(Bukkit.getOfflinePlayer("    "));
        objective.getScore("    ").setScore(5);
        objective.getScore("           ").setScore(4);

        final String title = ChatColor.translateAlternateColorCodes('&', scoreboardSettings.getString(Strings.CONFIG_SCOREBOARD_DISPLAY_NAME));

        new BukkitRunnable() {
            public void run() {
                try {
                    objective.setDisplayName(title);
                    reputation.setPrefix(String.valueOf(ReputationManager.getReputation(player)));
                    level.setPrefix(String.valueOf(ReputationManager.getReputationLevel(player)));
                    nextLevel.setPrefix(String.valueOf(ReputationManager.getReputation(player)) + "/" + String.valueOf(ReputationManager.getReputation(player) + ReputationManager.getNextLevel(player)));
                    balance.setPrefix(String.valueOf(CustomGUIShop.getInstance().getEconomy().getBalance(player)));
                    objective2.getScore(player).setScore(ReputationManager.getReputationLevel(player));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(CustomGUIShop.getInstance(), 0, 5);
        player.setScoreboard(scoreboard);
    }
}
