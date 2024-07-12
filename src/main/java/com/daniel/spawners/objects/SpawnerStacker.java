package com.daniel.spawners.objects;

import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

@Getter
public class SpawnerStacker {

    private final EntityType entityType;
    private final CreatureSpawner creatureSpawner;
    private int amount;
    private int spawnCount, maxDalay, minDalay;

    public SpawnerStacker(Location location, EntityType entityType, int amount, int spawnCount, int maxDalay, int minDalay) {
        location.getWorld().getBlockAt(location).setType(Material.MOB_SPAWNER);
        this.creatureSpawner = (CreatureSpawner) location.getWorld().getBlockAt(location).getState();
        this.amount = amount;
        this.spawnCount = spawnCount;
        this.maxDalay = maxDalay;
        this.minDalay = minDalay;
        this.entityType = entityType;
    }

    public void create() {
        creatureSpawner.setSpawnedType(entityType);
        creatureSpawner.setDelay(0);
        updateTags();
        creatureSpawner.update();
    }

    public void increment(int toAdd) {
        amount += toAdd;
        updateTags();
        creatureSpawner.update();
    }

    private void updateTags() {
        creatureSpawner.setDelay(0);
        NBTTileEntity tileEntity =  new NBTTileEntity(creatureSpawner);

        int adjustedSpawnCount = spawnCount * amount;
        int min = minDalay * adjustedSpawnCount / amount;
        int max = maxDalay * adjustedSpawnCount / amount;

        tileEntity.setInteger("MinSpawnDelay", min);
        tileEntity.setInteger("MaxSpawnDelay", max);

        tileEntity.setInteger("SpawnCount", adjustedSpawnCount);
        creatureSpawner.update();
    }

}
