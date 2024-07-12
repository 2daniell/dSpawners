package com.daniel.spawners.listeners;

import com.daniel.spawners.Main;
import com.daniel.spawners.db.Database;
import com.daniel.spawners.enums.Permission;
import com.daniel.spawners.handler.InstalledHandler;
import com.daniel.spawners.handler.SpawnerHandler;
import com.daniel.spawners.inventories.MainInventory;
import com.daniel.spawners.model.Spawner;
import com.daniel.spawners.model.SpawnerInstalled;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SpawnerListener implements Listener {

    private final InstalledHandler installeds;

    public SpawnerListener(InstalledHandler installeds) {
        this.installeds = installeds;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent e) {
        if (e.getItemInHand() == null || !(e.getItemInHand().hasItemMeta())) return;

        NBTItem nbtItem = new NBTItem(e.getItemInHand());
        if (!nbtItem.hasTag("spawner_type")) return;

        String type = nbtItem.getString("spawner_type");
        Spawner spawner = SpawnerHandler.findByName(type);

        if (spawner == null) return;
        if (!e.getPlayer().hasPermission(Main.getSpawnerConfig(spawner.getName()).getString("Permission.Place"))) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Main.config().getString("Message.NoPermPlace").replace('&', '§'));
            return;
        }

        if (!Main.config().getStringList("Worlds").contains(e.getPlayer().getWorld().getName())) {
            e.getPlayer().sendMessage(Main.config().getString("Message.ErrorWorldMessage").replace('&', '§'));
            e.setCancelled(true);
            return;
        }

        ItemStack inHand = e.getPlayer().getItemInHand();

        Player player = e.getPlayer();

        SpawnerInstalled installed = installeds.findNearbyBlocks(e.getBlockPlaced().getLocation(), player);
        if (installed == null) {

            e.setCancelled(true);

            int toAdd = 1;

            if (player.isSneaking()) {
                toAdd = inHand.getAmount();
            }

            if (inHand.getAmount() == toAdd) {
                player.setItemInHand(null);
            } else {
                player.getItemInHand().setAmount(inHand.getAmount() - toAdd);
            }

            int stack = toAdd;

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                SpawnerInstalled spawnerInstalled = new SpawnerInstalled(e.getPlayer().getUniqueId(), spawner, e.getBlockPlaced().getLocation(), stack);
                spawnerInstalled.create();
                installeds.add(spawnerInstalled);
                Database.persist(spawnerInstalled);
            });

        } else if (!(spawner.getType().equals(installed.getSpawner().getType()))) {

            e.setCancelled(true);
            player.sendMessage(Main.config().getString("Message.SpawnerNearby").replace('&', '§'));

        } else {

            e.setCancelled(true);

            int limitConfig = Main.config().getInt("BlockStackLimit");
            int stack = installed.getSpawnerStacker().getAmount();

            int limit = limitConfig - stack;

            int toAdd = 1;

            if (stack == limitConfig) return;


            if (player.isSneaking()) {
                toAdd = inHand.getAmount();
            }

            if (toAdd > limit) {
                int res = toAdd - limit;
                player.getItemInHand().setAmount(res);
                toAdd -= res;
            } else if(inHand.getAmount() == toAdd) {
                player.setItemInHand(null);
            } else {
                player.getItemInHand().setAmount(inHand.getAmount()-1);
            }
            installed.getSpawnerStacker().increment(toAdd);
            //installed.getHologram().updateHologram();

        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        SpawnerInstalled installed = installeds.findByLocation(e.getBlock().getLocation());
        if (installed == null) return;

        Player player = e.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) {

            e.setCancelled(true);
            e.getPlayer().sendMessage(Main.config().getString("Message.BreakSpawnerInCreative").replace('&', '§'));
            return;
        }

        if (installed.getOwner().equals(player.getUniqueId()) || installed.hasPermission(player, Permission.MODIFY) || player.hasPermission("spawners.admin")) {

            int amount = installed.getSpawnerStacker().getAmount();
            List<ItemStack> i = SpawnerHandler.findByType(installed.getSpawner().getType()).getItem(amount);

            int slotNeed = i.size();
            if(player.getInventory().firstEmpty() >= slotNeed) {

                installed.remove();
                installeds.remove(installed);

                player.sendMessage(Main.config().getString("Message.RemovedSpawner").replace('&', '§'));

                i.forEach(drop -> player.getInventory().addItem(drop));
                player.updateInventory();

                e.setExpToDrop(0);

                Database.delete(installed);

            } else {

                player.sendMessage(Main.config().getString("Message.InventoryFull").replace('&', '§'));
                e.setCancelled(true);

            }

        } else {
            player.sendMessage(Main.config().getString("Message.NoBreakSpawner").replace('&', '§'));
            e.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEvent(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof ArmorStand)) return;
        ItemStack inHand = e.getPlayer().getItemInHand();

        if (inHand == null || inHand.getType() == Material.AIR) return;

        NBTItem nbtItem = new NBTItem(inHand);
        if (!nbtItem.hasTag("spawner_type")) return;
        Spawner s = SpawnerHandler.findByName(nbtItem.getString("spawner_type"));
        if (s == null) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Location loc = e.getClickedBlock().getLocation();
        SpawnerInstalled installed = installeds.findByLocation(loc);
        if (installed == null) return;

        Player player = e.getPlayer();

        if (installed.getOwner().equals(player.getUniqueId()) || player.hasPermission("spawners.admin") || installed.isFriend(player)) {
            player.openInventory(new MainInventory(player, installed).getInventory());
        }
    }

}
