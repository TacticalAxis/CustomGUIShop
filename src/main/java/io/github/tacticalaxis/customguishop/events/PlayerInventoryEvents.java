package io.github.tacticalaxis.customguishop.events;

import io.github.tacticalaxis.customguishop.CustomGUIShop;
import io.github.tacticalaxis.customguishop.commands.ShopCommand;
import io.github.tacticalaxis.customguishop.manager.ConfigurationManager;
import io.github.tacticalaxis.customguishop.manager.InventoryManager;
import io.github.tacticalaxis.customguishop.manager.ReputationManager;
import io.github.tacticalaxis.customguishop.util.Strings;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("ALL")
public class PlayerInventoryEvents implements Listener {

    // handle inventory interactions
    @EventHandler
    public void interact(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (event.getClickedInventory() != null) {
                if (inventoryIsLegit(event.getClickedInventory())) {
                    event.setCancelled(true);
                    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        return;
                    }
                    Player player = (Player) event.getWhoClicked();
                    ItemStack item = event.getCurrentItem();
                    if (item.hasItemMeta()) {
                        if (item.getItemMeta().getDisplayName() != null) {
                            if (item.getItemMeta().getDisplayName().equals(InventoryManager.nextButton)) {
                                for (String s : CustomGUIShop.inventories.keySet()) {
                                    if (CustomGUIShop.inventories.get(s).contains(event.getClickedInventory())) {
                                        InventoryManager.nextPage(s, getPage(event.getClickedInventory().getTitle()), player);
                                    }
                                }
                            } else if (item.getItemMeta().getDisplayName().equals(InventoryManager.previousButton)) {
                                for (String s : CustomGUIShop.inventories.keySet()) {
                                    if (CustomGUIShop.inventories.get(s).contains(event.getClickedInventory())) {
                                        InventoryManager.previousPage(s, getPage(event.getClickedInventory().getTitle()), player);
                                    }
                                }
                            } else if (item.getItemMeta().getDisplayName().equals(InventoryManager.exitButton)) {
                                player.closeInventory();
                            }
                        } else {
                            buyItem(player, item);
                        }
                    }
                } else {
                    if (event.getView().getBottomInventory().getType() == InventoryType.PLAYER) {
                        if (inventoryIsLegit(event.getView().getTopInventory())) {
                            if (!event.isShiftClick()) {
                                if (event.isLeftClick() || event.isRightClick()) {
                                    if (!(isShop(event.getView().getTopInventory().getTitle()) == null)) {
                                        ItemStack itemInQuestion = ShopCommand.itemInAnyShop(event.getCurrentItem().getTypeId());
                                        if (itemInQuestion != null) {
                                            sellItem((Player) event.getWhoClicked(), itemInQuestion);
                                            ((Player) event.getWhoClicked()).updateInventory();
                                        }
                                    }
                                }
                            }
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    // check if an inventory title is a shop
    public static String isShop(String inventoryTitle) {
        String[] splitWord = inventoryTitle.split(" ");
        StringBuilder newVal = new StringBuilder();
        for (int i = 0; i < (splitWord.length - 2); i++) {
            newVal.append(splitWord[i]);
        }
        String finalWord = newVal.toString().trim();
        finalWord = finalWord.substring(0, finalWord.length() - 2);
        for (String shopName : ConfigurationManager.getInstance().getShopConfiguration().getKeys(false)) {
            String test = ChatColor.translateAlternateColorCodes('&', ConfigurationManager.getInstance().getShopConfiguration().getConfigurationSection(shopName).getString(Strings.SHOP_DISPLAY_NAME));
            if (test.equalsIgnoreCase(finalWord)) {
                return shopName;
            }
        }
        return null;
    }

    // buy an item
    public static void buyItem(Player player, ItemStack item) {
        double price = Double.parseDouble(item.getItemMeta().getLore().get(0).split(" \\$")[1]);
        int levelRequired = Integer.parseInt(item.getItemMeta().getLore().get(1).split(" ")[1]);
        ConfigurationSection config = ConfigurationManager.getInstance().getMainConfiguration().getConfigurationSection(Strings.CONFIG_SHOP_SECTION);
        if (ReputationManager.getReputationLevel(player) >= levelRequired) {
            EconomyResponse er = CustomGUIShop.getInstance().getEconomy().withdrawPlayer(player, price);
            ItemStack itemStackToGive = new ItemStack(Material.getMaterial(item.getTypeId()));
            if (!er.transactionSuccess()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(Strings.CONFIG_SHOP_TRANSACTION_FAIL)));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(Strings.CONFIG_SHOP_TRANSACTION_SUCCESS)));
                if (PlayerInventoryEvents.hasAvailableSlot(player)) {
                    itemStackToGive.setItemMeta(null);
                    player.getInventory().addItem(itemStackToGive);
                } else {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigurationManager.getInstance().getMainConfiguration().getConfigurationSection(Strings.CONFIG_SHOP_SECTION).getString(Strings.CONFIG_SHOP_BUY).replace("%amount%", String.valueOf(price)).replace("%item%", item.getType().toString().toLowerCase().replace("_", ": "))));
//                ReputationManager.addReputation(player, ConfigurationManager.getInstance().getMainConfiguration().getConfigurationSection(Strings.CONFIG_REPUTATION_SECTION).getInt(Strings.CONFIG_REPUTATION_ON_BUY), "buying", item.getType().toString().toLowerCase().replace("_", ": "));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(Strings.CONFIG_SHOP_LEVEL_TOO_LOW).replace("%required%", String.valueOf(levelRequired)).replace("%reason%", "buy")));
        }
    }

    // sell an item
    public static void sellItem(Player player, ItemStack item) {
        double price = Double.parseDouble(item.getItemMeta().getLore().get(0).split(" \\$")[1]) / 2;
        if (price < 2) {
            price = 0;
        }
        int levelRequired = Integer.parseInt(item.getItemMeta().getLore().get(1).split(" ")[1]);
        int reputationAdded = Integer.parseInt(item.getItemMeta().getLore().get(2).split(" ")[1]);
        ConfigurationSection config = ConfigurationManager.getInstance().getMainConfiguration().getConfigurationSection(Strings.CONFIG_SHOP_SECTION);
        if (ReputationManager.getReputationLevel(player) >= levelRequired) {
            if (playerHasItem(player, item)) {
                EconomyResponse er = CustomGUIShop.getInstance().getEconomy().depositPlayer(player, price);
                ItemStack itemStackToTake = new ItemStack(Material.getMaterial(item.getTypeId()));
                for (int i = 0; i < 36; i++) {
                    if (player.getInventory().getItem(i) != null) {
                        if (player.getInventory().getItem(i).getType() == itemStackToTake.getType()) {
                            if (player.getInventory().getItem(i).getAmount() == 1) {
                                player.getInventory().setItem(i, null);
                            } else {
                                player.getInventory().getItem(i).setAmount(player.getInventory().getItem(i).getAmount() - 1);
                            }
                            break;
                        }
                    }
                }
                if (!er.transactionSuccess()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(Strings.CONFIG_SHOP_TRANSACTION_SUCCESS)));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(Strings.CONFIG_SHOP_TRANSACTION_SUCCESS)));
                    ReputationManager.addReputation(player, reputationAdded, "selling", item.getType().toString().toLowerCase().replace("_", ": "));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigurationManager.getInstance().getMainConfiguration().getConfigurationSection(Strings.CONFIG_SHOP_SECTION).getString(Strings.CONFIG_SHOP_SELL).replace("%amount%", String.valueOf(price)).replace("%item%", item.getType().toString().toLowerCase().replace("_", ": "))));
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(Strings.CONFIG_SHOP_ITEM_NOT_FOUND)));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(Strings.CONFIG_SHOP_LEVEL_TOO_LOW).replace("%required%", String.valueOf(levelRequired)).replace("%reason%", "sell")));
        }
    }

    // check if a player has an item
    private static boolean playerHasItem(Player player, ItemStack item) {
        boolean has = false;
        for (ItemStack i : player.getInventory()) {
            if (i != null) {
                if (i.getType() == item.getType()) {
                    has = true;
                }
            }
        }
        return has;
    }

    // get the page number of the inventory
    private int getPage(String displayName) {
        String[] list = displayName.split(" ");
        String part = list[list.length - 1]; // 1)
        part = part.substring(0, part.length() - 1);
        return Integer.parseInt(part) - 1;
    }

    // check if the player has an available slot
    private static boolean hasAvailableSlot(Player player) {
        Inventory inv = player.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (item == null) {
                return true;
            }
        }
        return false;
    }

    // check if an inventory is a shop
    private boolean inventoryIsLegit(Inventory inventory) {
        for (String s : CustomGUIShop.inventories.keySet()) {
            if (CustomGUIShop.inventories.get(s).contains(inventory)) {
                return true;
            }
        }
        return false;
    }
}