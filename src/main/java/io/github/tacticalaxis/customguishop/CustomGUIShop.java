package io.github.tacticalaxis.customguishop;

import io.github.tacticalaxis.customguishop.commands.IDCommand;
import io.github.tacticalaxis.customguishop.commands.ReloadCommand;
import io.github.tacticalaxis.customguishop.commands.ReputationCommand;
import io.github.tacticalaxis.customguishop.commands.ShopCommand;
import io.github.tacticalaxis.customguishop.events.PlayerInventoryEvents;
import io.github.tacticalaxis.customguishop.events.PlayerExternalEvents;
import io.github.tacticalaxis.customguishop.manager.InventoryManager;
import io.github.tacticalaxis.customguishop.manager.ConfigurationManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomGUIShop extends JavaPlugin {

    private static CustomGUIShop instance;

    public static HashMap<String, ArrayList<Inventory>> inventories = new HashMap<String, ArrayList<Inventory>>();

    private Economy econ;

    // handle plugin enable
    @Override
    public void onEnable() {
        instance = this;
        ConfigurationManager.getInstance().setupConfiguration();
        getCommand("shop").setExecutor(new ShopCommand());
        getCommand("rep").setExecutor(new ReputationCommand());
        getCommand("reloadshop").setExecutor(new ReloadCommand());
        getCommand("id").setExecutor(new IDCommand());
        getServer().getPluginManager().registerEvents(new PlayerExternalEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerInventoryEvents(), this);
        inventories = InventoryManager.generateAll();

        // setup Vault
        if (!setupEconomy()) {
            this.getLogger().severe("Disabled, as Vault dependency was not found!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static CustomGUIShop getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return econ;
    }

    // setup economy
    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            System.out.println("Vault is null");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            System.out.println("RSP is null");
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}