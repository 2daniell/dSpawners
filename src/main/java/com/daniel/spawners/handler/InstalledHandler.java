package com.daniel.spawners.handler;

import com.daniel.spawners.Main;
import com.daniel.spawners.cache.Cache;
import com.daniel.spawners.db.Database;
import com.daniel.spawners.enums.Permission;
import com.daniel.spawners.model.SpawnerInstalled;
import com.daniel.spawners.objects.MobStacker;
import com.daniel.spawners.utils.Utils;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

public class InstalledHandler extends Cache<SpawnerInstalled> {

    public SpawnerInstalled findByLocation(Location location) {
        return find(e -> Utils.compare(e.getLocation(), location));
    }

    public SpawnerInstalled findSpawnerByEntity(Entity entity) {
        UUID entityUUID = entity.getUniqueId();
        return find(e -> {
            MobStacker stacker = e.getMobStacker();
            return stacker.getEntityUUID() != null && stacker.getEntityUUID().equals(entityUUID);
        });
    }

    public SpawnerInstalled findNearbyBlocks(Location location, Player player) {
        int radius = Main.config().getInt("Stack.StackArea");

        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {

                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() != Material.MOB_SPAWNER && (findByLocation(block.getLocation()) == null)) continue;

                    SpawnerInstalled installed = findByLocation(block.getLocation());

                    if (installed.getOwner().equals(player.getUniqueId()) || installed.hasPermission(player, Permission.MODIFY))
                        return installed;
                }
            }
        }
        return null;
    }

    public static void removeEntities(InstalledHandler handler) {
        for (SpawnerInstalled installed : handler.cacheSet) {
            Location location = installed.getMobStacker().getLocation();
            if (location != null) {
                Chunk chunk = location.getChunk();
                if (!chunk.isLoaded()) {
                    chunk.load();
                }
                Arrays.stream(chunk.getEntities()).filter(e -> e.getUniqueId().equals(installed.getMobStacker().getEntityUUID()))
                        .forEach(Entity::remove);
            } else {
                System.out.println("Localização do MOB gerador pelo spawner instalado na localização" + installed.getLocation() + " não encontrada OU nenhum MOB estava spawnado no momento.");
            }
        }
    }

    public void updadeHologram() {
        cacheSet.forEach(installed -> installed.getHologram().updateHologram());
    }

    public static void load(InstalledHandler handler) {
        handler.cacheSet.addAll(Database.load());
    }

    public static void remove(InstalledHandler handler) {
        handler.cacheSet.forEach(installed -> installed.getMobStacker().remove());
    }
}
