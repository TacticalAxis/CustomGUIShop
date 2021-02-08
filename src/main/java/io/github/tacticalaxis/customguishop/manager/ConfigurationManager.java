package io.github.tacticalaxis.customguishop.manager;

import io.github.tacticalaxis.customguishop.CustomGUIShop;
import io.github.tacticalaxis.customguishop.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigurationManager {

    private ConfigurationManager() {}

    static final ConfigurationManager instance = new ConfigurationManager();

    public static ConfigurationManager getInstance() {
        return instance;
    }

    private static final String mainConfigName = "config.yml";
    private static final String shopConfigName = "shops.yml";
    private static final String playerConfigName = "player-data.yml";

    private final ArrayList<String> ymlFiles = new ArrayList<String>();
    private final HashMap<String, FileConfiguration> configs = new HashMap<String, FileConfiguration>();

    FileConfiguration mainConfiguration;
    File mainFile;

    FileConfiguration shopConfiguration;
    File shopFile;

    FileConfiguration playerConfiguration;
    File playerFile;

    // main config setup
    public void setupConfiguration() {
        configTest(mainConfigName);
        configTest(shopConfigName);
        configTest(playerConfigName);

        mainFile = new File(CustomGUIShop.getInstance().getDataFolder(), mainConfigName);
        shopFile = new File(CustomGUIShop.getInstance().getDataFolder(), shopConfigName);
        playerFile = new File(CustomGUIShop.getInstance().getDataFolder(), playerConfigName);

        mainConfiguration = YamlConfiguration.loadConfiguration(mainFile);
        shopConfiguration = YamlConfiguration.loadConfiguration(shopFile);
        playerConfiguration = YamlConfiguration.loadConfiguration(playerFile);

        ymlFiles.add(mainConfigName);
        ymlFiles.add(playerConfigName);
        ymlFiles.add(shopConfigName);

        configs.put(mainConfigName, mainConfiguration);
        configs.put(playerConfigName, mainConfiguration);
        configs.put(shopConfigName, mainConfiguration);

        if (playerConfiguration.get(Strings.CONFIG_REPUTATION_SECTION) == null) {
            playerConfiguration.createSection(Strings.CONFIG_REPUTATION_SECTION);
        }
        savePlayerConfiguration();
    }

    // test if config exists, if not, create files
    private static void configTest(String name) {
        CustomGUIShop main = CustomGUIShop.getInstance();
        try {
            if (!main.getDataFolder().exists()) {
                boolean success = main.getDataFolder().mkdirs();
                if (!success) {
                    System.out.println("Configuration files could not be created!");
                    Bukkit.shutdown();
                }
            }
            File file = new File(main.getDataFolder(), name);
            if (!file.exists()) {
                main.getLogger().info(name + " not found, creating!");
                main.saveResource(name, true);
            } else {
                main.getLogger().info(name + " found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MAIN CONFIGURATION
    public FileConfiguration getMainConfiguration() {
        return mainConfiguration;
    }

    public void saveMainConfiguration() {
        try {
            shopConfiguration.save(shopFile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe("Could not save " + mainConfigName + "!");
        }
    }

    public void reloadMainConfig() {
        mainConfiguration = YamlConfiguration.loadConfiguration(mainFile);
    }

    // SHOP CONFIGURATION
    public FileConfiguration getShopConfiguration() {
        return shopConfiguration;
    }

    public void saveShopConfiguration() {
        try {
            shopConfiguration.save(shopFile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe("Could not save " + shopConfigName + "!");
        }
    }

    public void reloadShopConfiguration() {
        shopConfiguration = YamlConfiguration.loadConfiguration(shopFile);
    }

    // PLAYER CONFIGURATION
    public FileConfiguration getPlayerConfiguration() {
        return playerConfiguration;
    }

    public void savePlayerConfiguration() {
        try {
            playerConfiguration.save(playerFile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe("Could not save " + playerConfigName + "!");
        }
    }

    public void reloadPlayerConfiguration() {
        playerConfiguration = YamlConfiguration.loadConfiguration(playerFile);
    }

    public void reloadConfigurations() {
        for (String ymlFile : this.ymlFiles) {
            try {
                this.configs.get(ymlFile).load(new File(CustomGUIShop.getInstance().getDataFolder(), ymlFile));
            } catch (Exception ignored) {
            }
        }
        setupConfiguration();
    }
}