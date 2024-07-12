package com.daniel.spawners.inventories;

import com.daniel.spawners.Main;
import com.daniel.spawners.api.ItemBuilder;
import com.daniel.spawners.api.SkullCreator;
import com.daniel.spawners.menu.Menu;
import com.daniel.spawners.model.SpawnerInstalled;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.UUID;

public class ConfirmInventory extends Menu {

    private final SpawnerInstalled installed;
    private final UUID id;

    private final static String LEVELUP_SOUND = Main.getVersion().value >= 13 ? "ENTITY_PLAYER_LEVELUP" : "LEVEL_UP";

    public ConfirmInventory(Player player, SpawnerInstalled installed, UUID id) {
        super(player, "Deseja Remover?", 3*9);
        this.installed = installed;
        this.id = id;
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        ItemStack clicked = e.getCurrentItem();

        if (!clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String itemName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (itemName.equalsIgnoreCase("Aceitar")) {

            player.playSound(player.getLocation(), Sound.valueOf(LEVELUP_SOUND), 2f, 2f);
            installed.getTargetPermissions().remove(id);

            player.openInventory(new FriendsInventory(player, installed).getInventory());

        } else if (itemName.equalsIgnoreCase("Negar")) {

            player.openInventory(new FriendsInventory(player, installed).getInventory());

        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void setItens(Inventory inventory) {

        if (Main.getVersion().value >= 13) {
            inventory.setItem(11, new ItemBuilder(Material.valueOf("LIME_WOOL")).setDisplayName("§eAceitar").build());
            inventory.setItem(15, new ItemBuilder(Material.valueOf("RED_WOOL")).setDisplayName("§eNegar").build());
        } else {
            inventory.setItem(11, new ItemBuilder(Material.WOOL, (short) 5).setDisplayName("§eAceitar").build());
            inventory.setItem(15, new ItemBuilder(Material.WOOL, (short) 14).setDisplayName("§eNegar").build());
        }

        inventory.setItem(13, new ItemBuilder(SkullCreator.itemFromName(Bukkit.getOfflinePlayer(id).getName()))
                .setDisplayName("§e" + Bukkit.getOfflinePlayer(id).getName()).build());
    }
}
