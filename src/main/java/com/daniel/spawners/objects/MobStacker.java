package com.daniel.spawners.objects;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class MobStacker {

    private EntityType entityType;
    private Entity spawned;
    private UUID entityUUID;
    private int amount;

    private boolean useIA;
    private int maxStack;

    public MobStacker(EntityType entityType, int maxStack, boolean useIA) {
        this.entityType = entityType;
        this.useIA = useIA;
        this.maxStack = maxStack;
    }

    public boolean hasSpawned() {
        return entityUUID != null && getSpawned() != null && getSpawned().isValid() && amount > 0;
    }

    public void increment() {
        if ((amount + 1) >= maxStack || (amount >= maxStack)) {
            amount = maxStack;
        } else {
            amount++;
        }
        update();
    }

    public void decrement() {
        amount--;
        spawned = spawned.getLocation().getWorld().spawnEntity(spawned.getLocation(), entityType);
        this.entityUUID = spawned.getUniqueId();
        update();
    }

    public void start(Location spawnLocation) {
        if (amount > 0) {
            this.spawned = spawnLocation.getWorld().spawnEntity(spawnLocation, entityType);
            this.entityUUID = spawned.getUniqueId();
            spawned.setCustomNameVisible(true);
            update();
        }
    }

    public void remove() {
        if (spawned != null && spawned.isValid()) spawned.remove();
        amount = 0;
        spawned = null;
    }

    private void update() {
        if (!useIA) {
            NBTEntity nbtEntity = new NBTEntity(spawned);
            nbtEntity.setByte("NoAI", (byte)1);
        }
        spawned.setCustomName("§e" + amount + "x");
    }

    public Location getLocation() {
        if (spawned == null) return null;
        return spawned.getLocation();
    }
}
