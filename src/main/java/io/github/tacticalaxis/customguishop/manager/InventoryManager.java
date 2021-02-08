package io.github.tacticalaxis.customguishop.manager;

import io.github.tacticalaxis.customguishop.CustomGUIShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("ALL")
public class InventoryManager {

    public static final String previousButton = ChatColor.BLUE + "" + ChatColor.BOLD + "Previous Page";
    public static final String exitButton = ChatColor.RED + "" + ChatColor.BOLD + "Exit";
    public static final String nextButton = ChatColor.GREEN + "" + ChatColor.BOLD + "Next Page";

    // open shop inventory
    public void openInventory(String inventoryName, Player player) {
        player.openInventory(CustomGUIShop.inventories.get(inventoryName).get(0));
    }

    // go to next page of inventory
    public static void nextPage(String inventoryName, int page, Player player) {
        try {
            Inventory inv = CustomGUIShop.inventories.get(inventoryName).get(page + 1);
            player.openInventory(inv);
        } catch (ArrayIndexOutOfBoundsException ignored) {} catch (IndexOutOfBoundsException ignored) {}
    }

    // go to previous page of inventory
    public static void previousPage(String inventoryName, int page, Player player) {
        try {
            Inventory inv = CustomGUIShop.inventories.get(inventoryName).get(page - 1);
            player.openInventory(inv);
        } catch (ArrayIndexOutOfBoundsException ignored) {} catch (IndexOutOfBoundsException ignored) {}
    }

    // generate inventories
    public static HashMap<String, ArrayList<Inventory>> generateAll() {
        HashMap<String, ArrayList<Inventory>> list = new HashMap<String, ArrayList<Inventory>>();
        int count = 0;
        int currentInv = 0;
        for (String inventoryName : ConfigurationManager.getInstance().getShopConfiguration().getKeys(false)) {
            ArrayList<ItemStack> inventoryItems = new ArrayList<ItemStack>();
            ArrayList<Inventory> inventories = new ArrayList<Inventory>();
            String displayName = ChatColor.translateAlternateColorCodes('&', ConfigurationManager.getInstance().getShopConfiguration().getConfigurationSection(inventoryName).getString("display-name"));

            // add items to total list
            ConfigurationSection items = ConfigurationManager.getInstance().getShopConfiguration().getConfigurationSection(inventoryName).getConfigurationSection("items");
            for (String item : items.getKeys(false)) {
                ItemStack itemStack = new ItemStack(Material.getMaterial(items.getConfigurationSection(item).getInt("item-id")));
                ItemMeta im = itemStack.getItemMeta();
                ArrayList<String> lore = new ArrayList<String>();
                lore.add("§cPrice: $" + items.getConfigurationSection(item).getInt("price"));
                lore.add("§aLvl. " + items.getConfigurationSection(item).getInt("level-required"));
                lore.add("§eReputation: " + items.getConfigurationSection(item).getInt("reputation"));
                im.setLore(lore);
                itemStack.setItemMeta(im);
                inventoryItems.add(itemStack);
            }

            // add to individual inventories
            for (ItemStack i : inventoryItems) {
                try {
                    Inventory tmp = inventories.get(currentInv);
                } catch (IndexOutOfBoundsException e) {
                    inventories.add(Bukkit.createInventory(null, 54, displayName.trim() + ChatColor.RESET + " (Page " + (currentInv + 1) + ")"));
                }
                if (count <= 35) {
                    count += 1;
                } else {
                    count = 0;
                    addButtons(inventories.get(currentInv));
                    currentInv += 1;
                    inventories.add(Bukkit.createInventory(null, 54, displayName.trim() + ChatColor.RESET + " (Page " + (currentInv + 1) + ")"));
                }
                inventories.get(currentInv).addItem(i);
            }
            addButtons(inventories.get(currentInv));
            list.put(inventoryName, inventories);
            count = 0;
            currentInv = 0;
        }
        return list;
    }

    // add buttons to inventory
    private static void addButtons(Inventory current) {
        ItemStack is;
        for (int i = 36; i <= 53; i++) {
            if (i == 48) {
                // previous page
                is = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(previousButton);
                is.setItemMeta(im);
                current.setItem(i, is);
            } else if (i == 49) {
                // exit
                is = new ItemStack(Material.REDSTONE_BLOCK);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(exitButton);
                is.setItemMeta(im);
                current.setItem(i, is);
            } else if (i == 50) {
                // next page
                is = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(nextButton);
                is.setItemMeta(im);
                current.setItem(i, is);
            } else {
                // default
                is = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 8);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(" ");
                is.setItemMeta(im);
                current.setItem(i, is);
            }
        }
    }
}
