package com.daniel.spawners.listeners;

import com.daniel.spawners.Main;
import com.daniel.spawners.enums.Permission;
import com.daniel.spawners.handler.InstalledHandler;
import com.daniel.spawners.model.Spawner;
import com.daniel.spawners.model.SpawnerInstalled;
import com.daniel.spawners.objects.Commands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class EntityListener implements Listener {

    private final InstalledHandler installeds;

    public EntityListener(InstalledHandler installeds) {
        this.installeds = installeds;
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) return;
        Entity entity = e.getEntity();
        SpawnerInstalled installed = installeds.findSpawnerByEntity(entity);
        if (installed == null) return;
        if (!(e.getDamager() instanceof Player)) {
            e.setCancelled(true);
            return;
        }

        Player player = (Player) e.getDamager();
        if(!(installed.getOwner().equals(player.getUniqueId()) || installed.hasPermission(player, Permission.ATTACK))) {
            e.setCancelled(true);
            player.sendMessage(Main.config().getString("Message.NoPermAttack").replace('&', '§'));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        SpawnerInstalled installed = installeds.findSpawnerByEntity(entity);
        if (installed == null) return;
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) e.setDamage(0);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.JOCKEY)) e.getEntity().remove();
        if (e.getEntity().getType() == EntityType.ZOMBIE && e.getEntity().getPassenger() != null &&
                e.getEntity().getPassenger().getType() == EntityType.CHICKEN) e.getEntity().remove();
        if (e.getEntity().getType() == EntityType.CHICKEN) e.getEntity().remove();
    }

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent e) {
        SpawnerInstalled installed = installeds.findByLocation(e.getSpawner().getLocation());
        if (installed == null) return;

        if (e.getEntity().getType() == EntityType.CHICKEN && e.getEntity().getPassenger() != null && e.getEntity().getPassenger().getType() == EntityType.ZOMBIE) {
            e.getEntity().remove();
            e.setCancelled(true);
            return;
        }
        if (e.getEntityType() == EntityType.CHICKEN) {
            e.setCancelled(true);
            return;
        }

        if (installed.getMobStacker().hasSpawned()) {
            e.setCancelled(true);
            installed.getMobStacker().increment();
        } else {
            e.setCancelled(true);
            installed.getMobStacker().setAmount(1);
            installed.getMobStacker().start(e.getSpawner().getLocation().add(2, 0, 0));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) return;

        Entity entity = e.getEntity();
        SpawnerInstalled installed = installeds.findSpawnerByEntity(entity);
        if (installed == null) return;

        e.getDrops().clear();

        Player player = e.getEntity().getKiller();
        Spawner spawner = installed.getSpawner();

        if (player.isSneaking()) {

            if (!player.hasPermission("spawner.killall")) {
                player.sendMessage("§cVocê não possui permissão para matar todo o stack.");
            } else {

                int stackAmount = installed.getMobStacker().getAmount();

                if (stackAmount < 1) return;

                for (int i = 0; i < stackAmount; i++) {
                    ItemStack drop = installed.getSpawner().getDrop();
                    if (drop != null) {
                        installed.getStorage().add(drop);
                    }
                }

                installed.getMobStacker().remove();

                int commandsSize = spawner.getCommands().size();
                Commands command = spawner.getCommands().get(new Random().nextInt(commandsSize));

                double random = new Random().nextDouble() * 100;

                if (random <= command.getProb()) {

                    List<String> commands = command.getCommands();
                    commands.replaceAll(msg -> ChatColor.translateAlternateColorCodes('&', msg));
                    commands.replaceAll(msg -> msg.replaceAll("%player%", player.getName()));

                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            for (String cmd : commands) {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                            }

                        }
                    }.runTask(Main.getInstance());
                }
                return;
            }
        }

        if (installed.getMobStacker().hasSpawned() && installed.getMobStacker().getAmount() > 1) {

            installed.getMobStacker().decrement();

        } else if (installed.getMobStacker().getAmount() > 1) {

            installed.getMobStacker().decrement();

        } else {
            installed.getMobStacker().remove();
        }

        installed.getStorage().add(spawner.getDrop());

        int commandsSize = spawner.getCommands().size();
        Commands command = spawner.getCommands().get(new Random().nextInt(commandsSize));

        double random = new Random().nextDouble() * 100;

        if (random <= command.getProb()) {

            List<String> commands = command.getCommands();
            commands.replaceAll(msg -> ChatColor.translateAlternateColorCodes('&', msg));
            commands.replaceAll(msg -> msg.replaceAll("%player%", player.getName()));

            new BukkitRunnable() {
                @Override
                public void run() {

                    for (String cmd : commands) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    }

                }
            }.runTask(Main.getInstance());

        }
    }

}
