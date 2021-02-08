package io.github.tacticalaxis.customguishop.commands;

import io.github.tacticalaxis.customguishop.CustomGUIShop;
import io.github.tacticalaxis.customguishop.manager.ConfigurationManager;
import io.github.tacticalaxis.customguishop.manager.GameScoreboardManager;
import io.github.tacticalaxis.customguishop.manager.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("ALL")
public class ReloadCommand implements CommandExecutor {

    // this is the reload command
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // make 100% sure the config has reloaded
        ConfigurationManager.getInstance().reloadMainConfig();
        ConfigurationManager.getInstance().reloadShopConfiguration();
        ConfigurationManager.getInstance().reloadPlayerConfiguration();
        ConfigurationManager.getInstance().setupConfiguration();
        ConfigurationManager.getInstance().reloadConfigurations();
        ConfigurationManager.getInstance().saveMainConfiguration();
        ConfigurationManager.getInstance().saveShopConfiguration();
        ConfigurationManager.getInstance().savePlayerConfiguration();

        // generate new inventories (if the shops.yml has been updated)
        CustomGUIShop.inventories = InventoryManager.generateAll();

        // make sure everyone has the latest version of the scoreboard
        for (Player p : Bukkit.getOnlinePlayers()) {
            GameScoreboardManager gs = new GameScoreboardManager();
            gs.setScoreboard(p);
        }

        return true;
    }
}
