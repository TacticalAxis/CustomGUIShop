package io.github.tacticalaxis.customguishop.commands;

import io.github.tacticalaxis.customguishop.manager.ConfigurationManager;
import io.github.tacticalaxis.customguishop.manager.ReputationManager;
import io.github.tacticalaxis.customguishop.util.Strings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("ALL")
public class ReputationCommand implements CommandExecutor {

    // this is the reputation command
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigurationManager.getInstance().getMainConfiguration().getConfigurationSection(Strings.CONFIG_REPUTATION_SECTION).getString(Strings.CONFIG_REPUTATION_ON_QUERY_MESSAGE).replace("%reputation%", String.valueOf(ReputationManager.getReputation(player))).replace("%level%",String.valueOf(ReputationManager.getReputationLevel(player)))));
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command");
        }
        return true;
    }
}