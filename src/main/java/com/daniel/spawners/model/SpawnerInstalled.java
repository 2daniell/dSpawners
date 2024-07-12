package com.daniel.spawners.model;

import com.daniel.spawners.enums.Permission;
import com.daniel.spawners.handler.HologramHandler;
import com.daniel.spawners.handler.InstalledHandler;
import com.daniel.spawners.objects.Hologram;
import com.daniel.spawners.objects.MobStacker;
import com.daniel.spawners.objects.SpawnerStacker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class SpawnerInstalled {

    private UUID owner;
    private Spawner spawner;
    private Location location;
    private List<ItemStack> storage;
    private Map<UUID, Set<Permission>> targetPermissions;
    private MobStacker mobStacker;
    private SpawnerStacker spawnerStacker;
    private Hologram hologram;

    public SpawnerInstalled(UUID owner, Spawner spawner, Location location, List<ItemStack> storage, Map<UUID, Set<Permission>> targetPermissions, int stack) {
        this.owner = owner;
        this.spawner = spawner;
        this.location = location;
        this.storage = storage;
        this.targetPermissions = targetPermissions;
        this.spawnerStacker = new SpawnerStacker(location, spawner.getType().getEntityType(), stack, spawner.getSpawnCount(), spawner.getMaxDalay(), spawner.getMinDalay());
        this.mobStacker = new MobStacker(spawner.getType().getEntityType(), spawner.getMaxStack(), spawner.isUseIA());
        //initHologram();
    }

    public SpawnerInstalled(UUID owner, Spawner spawner, Location location, int stack) {
        this.location = location;
        this.owner = owner;
        this.spawner = spawner;
        this.storage = new ArrayList<>();
        this.targetPermissions = new HashMap<>();
        this.spawnerStacker = new SpawnerStacker(location, spawner.getType().getEntityType(), stack, spawner.getSpawnCount(), spawner.getMaxDalay(), spawner.getMinDalay());
        this.mobStacker = new MobStacker(spawner.getType().getEntityType(), spawner.getMaxStack(), spawner.isUseIA());

    }

    public void create() {
        //initHologram();
        //hologram.create();
        spawnerStacker.create();
    }

    public void remove() {
        mobStacker.remove();
        //HologramHandler.removeHologram(hologram.getLocation().getChunk(), hologram);
        //hologram.removeHologram();
    }

    public boolean isFriend(Player player) {
        return targetPermissions.containsKey(player.getUniqueId());
    }

    public boolean hasPermission(Player player, Permission permission) {
        return targetPermissions.containsKey(player.getUniqueId()) && targetPermissions.get(player.getUniqueId()).contains(permission);
    }

    private void initHologram() {
        this.hologram = new Hologram(spawnerStacker, location, owner, spawner);
        HologramHandler.addHologram(hologram.getLocation().getChunk(), hologram);
    }
}
