package io.github.tacticalaxis.customguishop.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("ALL")
public class IDCommand implements CommandExecutor {

    // this is the ID command
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInHand();
            if (!(item == null)) {
                player.sendMessage(ChatColor.GREEN + "ID: " + ChatColor.GOLD + item.getTypeId());
            } else {
                player.sendMessage(ChatColor.RED + "You don't have anything in hand!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can user the /shop command");
        }
        return true;
    }
}
