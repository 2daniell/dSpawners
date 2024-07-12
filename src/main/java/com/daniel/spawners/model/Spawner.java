package com.daniel.spawners.model;

import com.daniel.spawners.Main;
import com.daniel.spawners.api.ItemBuilder;
import com.daniel.spawners.api.SkullCreator;
import com.daniel.spawners.enums.SpawnerType;
import com.daniel.spawners.objects.Commands;
import com.daniel.spawners.objects.Drop;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Getter
public class Spawner implements Serializable {

    private String name;
    private SpawnerType type;
    private String displayName;
    private List<String> lore;
    private List<Drop> drops;
    private List<Commands> commands;


    private boolean useIA;
    private int maxStack;
    private int spawnCount, maxDalay, minDalay;

    public Spawner(String name, SpawnerType type) {
        this.name = name;
        this.type = type;
        this.commands = new ArrayList<>();
        this.drops = new ArrayList<>();
    }

    public void load() {
        final FileConfiguration config = Main.getSpawnerConfig(name);
        loadItem(config);
        loadCommands(config);
        loadDrop(config);
        loadConfigs(config);
    }

    private void loadItem(FileConfiguration config) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', config.getString("Item.DisplayName"));
        this.lore = config.getStringList("Item.Lore").stream().map(e -> e.replace('&', '§')).collect(Collectors.toList());
    }

    private void loadCommands(FileConfiguration config) {
        for(String key : config.getConfigurationSection("Commands").getKeys(false)) {
            double prob = config.getDouble("Commands." + key + ".Prob");
            List<String> cmds = config.getStringList("Commands." + key + ".Execute");
            commands.add(new Commands(cmds, prob));
        }
    }

    private void loadDrop(FileConfiguration config) {
        if (config.contains("Drops")) {
            for (String key : config.getConfigurationSection("Drops").getKeys(false)) {
                String id = config.getString("Drops." + key + ".Id");
                int amount = config.getInt("Drops." + key + ".Amount");
                int prob = config.getInt("Drops." + key + ".Prob");
                drops.add(new Drop(id, amount, prob));
            }
        }
    }

    private void loadConfigs(FileConfiguration config) {
        this.useIA = config.getBoolean("UseAI");
        this.spawnCount = config.getInt("Gerador.SpawnCount");
        this.maxDalay = config.getInt("Gerador.MaxDalay");
        this.minDalay = config.getInt("Gerador.MinDalay");
        this.maxStack = config.getInt("Gerador.MaxStack");
    }

    public ItemStack getDrop() {
        int totalProbability = drops.stream().mapToInt(Drop::getProb).sum();
        int value = new Random().nextInt(totalProbability);
        int ac = 0;
        for (Drop drop : drops) {
            ac += drop.getProb();
            if (value < ac) {
                return drop.getItem();
            }
        }
        return null;
    }

    public List<ItemStack> getItem(int amount) {
        List<ItemStack> items = new ArrayList<>();

        if (amount <= 64) {
            ItemStack spawner = new ItemBuilder(SkullCreator.itemFromBase64(type.getUrl())).setDisplayName(displayName).setLore(lore).setAmount(amount).build();
            NBTItem nbtItem = new NBTItem(spawner); nbtItem.setString("spawner_type", type.getName());
            nbtItem.applyNBT(spawner);
            items.add(spawner);
        } else {
            int fullStacks = amount / 64;
            int remainingAmount = amount % 64;
            for (int i = 0; i < fullStacks; i++) {
                ItemStack spawner = new ItemBuilder(SkullCreator.itemFromBase64(type.getUrl())).setDisplayName(displayName).setLore(lore).setAmount(64).build();
                NBTItem nbtItem = new NBTItem(spawner); nbtItem.setString("spawner_type", type.getName());
                nbtItem.applyNBT(spawner);
                items.add(spawner);
            }
            if (remainingAmount > 0) {
                ItemStack spawner = new ItemBuilder(SkullCreator.itemFromBase64(type.getUrl())).setDisplayName(displayName).setLore(lore).setAmount(remainingAmount).build();
                NBTItem nbtItem = new NBTItem(spawner); nbtItem.setString("spawner_type", type.getName());
                nbtItem.applyNBT(spawner);
                items.add(spawner);
            }
        }
        return items;
    }

    public ItemStack getItem() {
        ItemStack spawner = new ItemBuilder(SkullCreator.itemFromBase64(type.getUrl())).setDisplayName(displayName).setLore(lore).build();
        NBTItem nbtItem = new NBTItem(spawner); nbtItem.setString("spawner_type", type.getName());
        nbtItem.applyNBT(spawner);
        return spawner;
    }
}
