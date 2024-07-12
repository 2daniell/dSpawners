package com.daniel.spawners.objects;

import com.daniel.spawners.Main;
import com.daniel.spawners.model.Spawner;
import com.daniel.spawners.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Hologram {

    private final SpawnerStacker stacker;
    private final UUID owner;
    private final Spawner spawner;

    private final Location location;
    private boolean isSpawned = false;

    private final Map<Location, ArmorStand> holograms;

    public Hologram(SpawnerStacker stacker, Location location, UUID owner, Spawner spawner) {
        this.stacker = stacker;
        this.owner = owner;
        this.spawner = spawner;
        this.location = location;
        this.holograms = new HashMap<>();
    }

    public void create() {
        if (isSpawned) return;
        double yOffset = 0.0;

        for (String line : hologramLines()) {

            ArmorStand hologramStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, yOffset, 0.5), EntityType.ARMOR_STAND);

            hologramStand.setVisible(false);
            hologramStand.setCustomNameVisible(true);
            hologramStand.setCustomName(line);
            hologramStand.setArms(false);
            hologramStand.setBasePlate(false);
            hologramStand.setGravity(false);

            holograms.put(hologramStand.getLocation(), hologramStand);

            yOffset -= 0.25;

        }
        this.isSpawned = true;
    }

    public void updateHologram() {
        removeHologram();
        create();
    }

    public void removeHologram() {
        this.isSpawned = false;
        holograms.values().forEach(Entity::remove);
        holograms.clear();
    }

    public List<String> hologramLines() {
        return Main.config().getStringList("Holograms").stream().map(line ->
                line.replace('&', '§')
                        .replaceAll("%owner%", Bukkit.getOfflinePlayer(owner).getName())
                        .replaceAll("%type%", Utils.capitalizeFirstLetter(spawner.getType().getName()))
                        .replaceAll("%stack%", String.valueOf(stacker.getAmount())))
                .collect(Collectors.toList());
    }
}
