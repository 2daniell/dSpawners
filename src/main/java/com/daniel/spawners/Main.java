package com.daniel.spawners;

import com.daniel.spawners.api.Version;
import com.daniel.spawners.cache.Cache;
import com.daniel.spawners.commands.Command;
import com.daniel.spawners.db.Database;
import com.daniel.spawners.handler.HologramHandler;
import com.daniel.spawners.handler.InstalledHandler;
import com.daniel.spawners.handler.SpawnerHandler;
import com.daniel.spawners.listeners.*;
import com.daniel.spawners.model.Spawner;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.Set;

@Getter
public class Main extends JavaPlugin {

    private static Main instance;

    private static Version version;

    private static final Map<String, FileConfiguration> spawnerConfig = Maps.newHashMap();

    private final InstalledHandler handler = new InstalledHandler();

    @Override
    public void onEnable() {
        instance = this;

        version = Version.getServerVersion();

        saveDefaultConfig();

        loadSpawnerConfig();
        register();

        Database.table();

        SpawnerHandler.load();

        InstalledHandler.load(handler);

        Cache.persistData(handler);
        //handler.updadeHologram();
    }

    @Override
    public void onDisable() {
        //HologramHandler.removeAll();
        InstalledHandler.removeEntities(handler);
        handler.persist();
    }

    public static void reloadConfigs() {
        spawnerConfig.clear();
        Main.getInstance().loadSpawnerConfig();

        Main.getInstance().reloadConfig();

        SpawnerHandler.load();
        //Main.getInstance().handler.updadeHologram();

        InstalledHandler.remove(Main.getInstance().handler);
    }

    public void register() {
        getCommand("spawners").setExecutor(new Command());

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new SpawnerListener(handler), this);
        pm.registerEvents(new PlotListener(handler), this);
        pm.registerEvents(new EntityListener(handler), this);
        //pm.registerEvents(new HologramListener(), this);
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new ChatListener(), this);
    }

    private void loadSpawnerConfig() {
        Set<Spawner> spawners = SpawnerHandler.getSpawners();

        File dataFolder = new File(getDataFolder(), "spawners");

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        for (Spawner spawner : spawners) {
            File configFile = new File(dataFolder, spawner.getName() + ".yml");

            if (!configFile.exists()) {
                saveResource("spawners/" + spawner.getName() + ".yml", false);
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            spawnerConfig.put(spawner.getName(), config);

            System.out.println("Configuração do spawners " + spawner.getName().toUpperCase() + " foi carregado.");
        }
    }

    public static FileConfiguration getSpawnerConfig(String name) {
        return spawnerConfig.get(name);
    }

    public static FileConfiguration config() {
        return Main.getInstance().getConfig();
    }

    public static Main getInstance() {
        return instance;
    }

    public static Version getVersion() {
        return version;
    }
}