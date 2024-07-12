package com.daniel.spawners.listeners;

import com.daniel.spawners.handler.InstalledHandler;
import com.daniel.spawners.model.SpawnerInstalled;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.plotsquared.bukkit.events.PlotClearEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class PlotListener implements Listener {
    private static final int MAX_Y = 256;

    private final InstalledHandler installeds;

    public PlotListener(InstalledHandler installeds) {
        this.installeds = installeds;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCLear(PlotClearEvent e) {
        Plot plot = e.getPlot();

        List<Location> corners = plot.getAllCorners();

        Location corner1 = null;
        Location corner2 = null;

        for (Location corner : corners) {
            if (corner1 == null || corner.getX() < corner1.getX() && corner.getZ() < corner1.getZ()) {
                corner1 = corner;
            }
            if (corner2 == null || corner.getX() > corner2.getX() && corner.getZ() > corner2.getZ()) {
                corner2 = corner;
            }
        }

        if (corner1 != null && corner2 != null) {

            int maxX = Math.max(corner1.getX(), corner2.getX());
            int maxZ = Math.max(corner1.getZ(), corner2.getZ());

            int minX = Math.min(corner1.getX(), corner2.getX());
            int minZ = Math.min(corner1.getZ(), corner2.getZ());

            for (int x = minX; x <= maxX; x++) {
                for (int y = 0; y <= MAX_Y; y++) {
                    for (int z = minZ; z <= maxZ; z++) {

                        World world = Bukkit.getWorld(corner1.getWorld());

                        Block block = world.getBlockAt(x, y, z);

                        SpawnerInstalled installed = installeds.findByLocation(block.getLocation());

                        if (installed == null) continue;


                        installed.remove();
                        //InstalledsManager.deleteDatabase(installed);
                        installeds.remove(installed);

                        block.breakNaturally();
                    }
                }
            }
        } else {
            System.out.println("Não foi possível encontrar quinas do plot.");
        }
    }
}
