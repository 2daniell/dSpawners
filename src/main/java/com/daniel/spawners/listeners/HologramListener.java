package com.daniel.spawners.listeners;

import com.daniel.spawners.Main;
import com.daniel.spawners.handler.HologramHandler;
import com.daniel.spawners.objects.Hologram;
import org.bukkit.Chunk;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class HologramListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        Chunk chunk = e.getChunk();
        if (!HologramHandler.hasChunk(chunk)) return;

        List<Hologram> holograms = HologramHandler.findByChunk(chunk);

        new BukkitRunnable() {
            @Override
            public void run() {
                holograms.forEach(Hologram::create);
            }
        }.runTask(Main.getInstance());

    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        Chunk chunk = e.getChunk();
        if (!HologramHandler.hasChunk(chunk)) return;

        List<Hologram> holograms = HologramHandler.findByChunk(chunk);

        holograms.forEach(Hologram::removeHologram);

    }

    @EventHandler
    public void onArmor(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) e.getRightClicked();
            Player player = e.getPlayer();
            if (armorStand.getCustomName() != null) e.setCancelled(true);
        }
    }
}
