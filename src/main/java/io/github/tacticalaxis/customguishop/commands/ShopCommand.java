package io.github.tacticalaxis.customguishop.commands;

import io.github.tacticalaxis.customguishop.events.PlayerInventoryEvents;
import io.github.tacticalaxis.customguishop.manager.InventoryManager;
import io.github.tacticalaxis.customguishop.manager.ConfigurationManager;
import io.github.tacticalaxis.customguishop.util.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class ShopCommand implements CommandExecutor {

    // this is the shop command
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        InventoryManager im = new InventoryManager();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length <= 0) {
                StringBuilder list = new StringBuilder();
                for (String s : ConfigurationManager.getInstance().getShopConfiguration().getKeys(false)) {
                    list.append(s).append(", ");
                }
                String message = list.substring(0, list.length() - 2);
                player.sendMessage(ChatColor.GREEN + "Shops: " + ChatColor.GOLD + message);
            } else if (args.length == 1) {
                if (testName(player, args[0]) != null) {
                    im.openInventory(testName(player, args[0]), player);
                } else {
                    player.sendMessage(ChatColor.GOLD + args[0] + ChatColor.RED + " is not a valid shop! Do /shop to see a list!");
                }
            } else if (args.length == 3) {
                if (testName(player, args[0]) != null) {
                    String shopName = testName(player, args[0]);
                    if (args[1].equalsIgnoreCase("sell")) {
                        try {
                            int id = Integer.parseInt(args[2]);
                            if (itemInShop(id, shopName) != null) {
                                PlayerInventoryEvents.sellItem(player, itemInShop(id, shopName));
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigurationManager.getInstance().getMainConfiguration().getConfigurationSection(Strings.CONFIG_SHOP_SECTION).getString(Strings.CONFIG_SHOP_NOT_IN_SHOP)));
                            }
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "Please make sure you have used a id number!");
                        }
                    } else if (args[1].equalsIgnoreCase("buy")) {
                        try {
                            int id = Integer.parseInt(args[2]);
                            if (itemInShop(id, shopName) != null) {
                                PlayerInventoryEvents.buyItem(player, itemInShop(id, shopName));
                            } else {
                                player.sendMessage(ChatColor.RED + "That item is not in the shop!");
                            }
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "Please make sure you have used an ID number!");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Format: /shop <shopname> <buy|sell> <id>(Do /id to find an item's ID)");
                    }
                } else {
                    player.sendMessage(ChatColor.GOLD + args[0] + ChatColor.RED + " is not a valid shop! Do /shop to see a list!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "please use /shop");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can user the /shop command");
        }
        return true;
    }

    // test that a shop name exists
    public static String testName(Player player, String assumedName) {
        String name = assumedName;
        boolean real = false;
        for (String s : ConfigurationManager.getInstance().getShopConfiguration().getKeys(false)) {
            if (name.toLowerCase().equalsIgnoreCase(s.toLowerCase())) {
                name = s;
                real = true;
                break;
            }
        }
        if (!real) {
            return null;
        } else {
            return name;
        }
    }

    // test if an item is in a particular shop
    public static ItemStack itemInShop(int id, String shopName) {
        ItemStack is = null;
        boolean valid = false;
        ConfigurationSection shopItems = ConfigurationManager.getInstance().getShopConfiguration().getConfigurationSection(shopName).getConfigurationSection("items");
        for (String s : shopItems.getKeys(false)) {
            if (id == shopItems.getConfigurationSection(s).getInt("item-id")) {
                is = new ItemStack(Material.getMaterial(id));
                ItemMeta im = is.getItemMeta();
                ArrayList<String> lore = new ArrayList<String>();
                lore.add("§cPrice: $" + shopItems.getConfigurationSection(s).getInt("price"));
                lore.add("§aLvl. " + shopItems.getConfigurationSection(s).getInt("level-required"));
                lore.add("§eReputation. " + shopItems.getConfigurationSection(s).getInt("reputation"));
                im.setLore(lore);
                is.setItemMeta(im);
                valid = true;
            }
        }
        if (!valid) {
            return null;
        } else {
            return is;
        }
    }

    // test if an item is in any shop
    public static ItemStack itemInAnyShop(int id) {
        for (String shop : ConfigurationManager.getInstance().getShopConfiguration().getKeys(false)) {
            for (String item : ConfigurationManager.getInstance().getShopConfiguration().getConfigurationSection(shop).getConfigurationSection("items").getKeys(false)) {
                ConfigurationSection itemData = ConfigurationManager.getInstance().getShopConfiguration().getConfigurationSection(shop).getConfigurationSection("items").getConfigurationSection(item);
                if (id == itemData.getInt("item-id")) {
                    ItemStack real = new ItemStack(Material.getMaterial(itemData.getInt("item-id")));
                    ItemMeta im = real.getItemMeta();
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add("§cPrice: $" + itemData.getInt("price"));
                    lore.add("§aLvl. " + itemData.getInt("level-required"));
                    lore.add("§eReputation. " + itemData.getInt("reputation"));
                    im.setLore(lore);
                    real.setItemMeta(im);
                    return real;
                }
            }
        }
        return null;
    }
}