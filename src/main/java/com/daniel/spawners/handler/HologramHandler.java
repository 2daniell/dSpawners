package com.daniel.spawners.handler;

import com.daniel.spawners.objects.Hologram;
import lombok.NoArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class HologramHandler {

    private static final Map<String, Hologram> holograms = new HashMap<>();

    public static void addHologram(Chunk chunk, Hologram hologram) {
        String chunkKey = getChunkKey(chunk);
        holograms.put(chunkKey, hologram);
    }

    public static void removeHologram(Chunk chunk, Hologram hologram) {
        hologram.getHolograms().values().forEach(Entity::remove);
        hologram.getHolograms().clear();
        String chunkKey = getChunkKey(chunk);
        holograms.remove(chunkKey, hologram);
    }

    public static boolean hasChunk(Chunk chunk) {
        String chunkKey = getChunkKey(chunk);
        return holograms.containsKey(chunkKey);
    }

    public static List<Hologram> findByChunk(Chunk chunk) {
        List<Hologram> list = new ArrayList<>();
        String chunkKey = getChunkKey(chunk);
        for (Hologram h : holograms.values()) {
            if (getChunkKey(h.getLocation().getChunk()).equals(chunkKey)) {
                list.add(h);
            }
        }
        return list;
    }

    public static void removeAll() {
        for (Hologram hologram : holograms.values()) {
            hologram.removeHologram();
        }
        holograms.clear();
    }

    private static String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

}
