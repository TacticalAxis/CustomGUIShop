package io.github.tacticalaxis.customguishop.events;

import io.github.tacticalaxis.customguishop.manager.ReputationManager;
import io.github.tacticalaxis.customguishop.manager.ConfigurationManager;
import io.github.tacticalaxis.customguishop.manager.GameScoreboardManager;
import io.github.tacticalaxis.customguishop.util.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@SuppressWarnings("ALL")
public class PlayerExternalEvents implements Listener {

    // handle player joins
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (ConfigurationManager.getInstance().getPlayerConfiguration().getConfigurationSection(Strings.CONFIG_REPUTATION_SECTION).get(p.getUniqueId().toString()) == null) {
            ConfigurationManager.getInstance().getPlayerConfiguration().getConfigurationSection(Strings.CONFIG_REPUTATION_SECTION).set(p.getUniqueId().toString(), 0);
        }
        ConfigurationManager.getInstance().savePlayerConfiguration();

        // give the player a scoreboard
        GameScoreboardManager gs = new GameScoreboardManager();
        gs.setScoreboard(event.getPlayer());
    }

    // handle deaths/kills
    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller().getPlayer();
            ReputationManager.addReputation(killer, ConfigurationManager.getInstance().getMainConfiguration().getConfigurationSection(Strings.CONFIG_REPUTATION_SECTION).getInt(Strings.CONFIG_REPUTATION_ON_KILL), "killing", event.getEntity().getDisplayName());
        }
    }
}
