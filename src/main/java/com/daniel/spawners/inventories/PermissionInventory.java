package com.daniel.spawners.inventories;

import com.daniel.spawners.Main;
import com.daniel.spawners.api.ItemBuilder;
import com.daniel.spawners.api.SkullCreator;
import com.daniel.spawners.enums.Permission;
import com.daniel.spawners.menu.Menu;
import com.daniel.spawners.model.SpawnerInstalled;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PermissionInventory extends Menu {

    private final SpawnerInstalled installed;
    private final UUID id;

    public PermissionInventory(Player player, SpawnerInstalled installed, UUID id) {
        super(player, "Gerenciar Permissões", 4*9);
        this.installed = installed;
        this.id = id;
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        ItemStack clicked = e.getCurrentItem();

        if (!clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String itemName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (itemName.equalsIgnoreCase("Voltar")) {

            player.openInventory(new FriendsInventory(player, installed).getInventory());

        } else {

            Permission permission = Permission.findByName(itemName);
            if (permission == null) return;

            Set<Permission> permissions = installed.getTargetPermissions().computeIfAbsent(id, k -> new HashSet<>());

            if (permissions.contains(permission)) {
                permissions.remove(permission);
            } else {
                permissions.add(permission);
            }

            player.openInventory(getInventory());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setItens(Inventory inventory) {

        inventory.setItem(27, new ItemBuilder(Material.ARROW).setDisplayName("§eVoltar").build());

        inventory.setItem(13, new ItemBuilder(SkullCreator.itemFromName(Bukkit.getOfflinePlayer(id).getName()))
                .setDisplayName("§e" + Bukkit.getOfflinePlayer(id).getName()).build());

        Set<Permission> permissions = installed.getTargetPermissions().computeIfAbsent(id, k -> new HashSet<>());

        int slot = 19;
        for (Permission permission : Permission.values()) {

            if (slot == 20) slot = 21;
            if (slot == 22) slot = 23;
            if (slot == 24) slot = 25;

            ItemStack item;

            if (permissions.contains(permission)) {

                if (Main.getVersion().value >= 13) {

                    item = new ItemBuilder(Material.valueOf("GREEN_DYE")).setDisplayName("§e" + permission.getName())
                            .setLore(permission.getDescription()).build();

                } else {

                    item = new ItemBuilder(Material.INK_SACK, (short) 10).setDisplayName("§e" + permission.getName())
                            .setLore(permission.getDescription()).build();

                }

            } else {

                if (Main.getVersion().value >= 13) {

                    item = new ItemBuilder(Material.valueOf("GRAY_DYE")).setDisplayName("§e" + permission.getName())
                            .setLore(permission.getDescription()).build();

                } else {

                    item = new ItemBuilder(Material.INK_SACK, (short) 8).setDisplayName("§e" + permission.getName())
                            .setLore(permission.getDescription()).build();

                }

            }

            inventory.setItem(slot++, item);

        }
    }
}
