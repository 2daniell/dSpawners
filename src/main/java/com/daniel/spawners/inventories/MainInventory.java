package com.daniel.spawners.inventories;

import com.daniel.spawners.Main;
import com.daniel.spawners.api.ItemBuilder;
import com.daniel.spawners.api.SkullCreator;
import com.daniel.spawners.enums.Permission;
import com.daniel.spawners.menu.Menu;
import com.daniel.spawners.model.SpawnerInstalled;
import com.daniel.spawners.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainInventory extends Menu {

    private final SpawnerInstalled installed;

    public MainInventory(Player player, SpawnerInstalled installed) {
        super(player, "Spawner de " + Bukkit.getOfflinePlayer(installed.getOwner()).getName(), 3*9);
        this.installed = installed;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        ItemStack clicked = e.getCurrentItem();

        if (!clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String itemName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (itemName.equalsIgnoreCase("Amigos")) {

            player.openInventory(new FriendsInventory(player, installed).getInventory());

        }

        if (itemName.equalsIgnoreCase("Armazem")) {

            if (player.getUniqueId().equals(installed.getOwner()) || installed.hasPermission(player, Permission.COLLECT)) {

                player.openInventory(new StorageInventory(player, installed).getInventory());

            } else {

                player.sendMessage(Main.config().getString("Message.NoPermCollect").replace('&', '§'));

            }

        } else if (itemName.startsWith("Gerador") && clicked.getItemMeta().hasLore() && clicked.getItemMeta().getLore().contains("§7Clique com §7§nesquerdo §7para resetar!")) {

            installed.getMobStacker().remove();
            player.sendMessage("§aMobs resetados!");

        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void setItens(Inventory inventory) {

        List<String> infoLore = new ArrayList<>(Arrays.asList(
                "",
                "§7Dono: §f" + Bukkit.getOfflinePlayer(installed.getOwner()).getName(),
                "§7Stack: §f" + installed.getSpawnerStacker().getAmount(),
                "",
                "  §7Amigos:",
                ""
        ));

        if (installed.getTargetPermissions().isEmpty()) {

            infoLore.add("   §cSem amigos adicionados!");

        } else {

            for (UUID id : installed.getTargetPermissions().keySet()) {

                String name = Bukkit.getOfflinePlayer(id).getName();

                infoLore.add("   §7- §f" + name);

            }

        }
        infoLore.add("");

        if (player.getUniqueId().equals(installed.getOwner()) || installed.hasPermission(player, Permission.ADMINISTER)) {

            inventory.setItem(10, new ItemBuilder(SkullCreator.itemFromName(Bukkit.getOfflinePlayer(installed.getOwner()).getName()))
                    .setDisplayName("§eAmigos").build());

            inventory.setItem(12, new ItemBuilder(Material.NETHER_STAR).setDisplayName("§eInformações").setLore(infoLore)
                    .build());

            inventory.setItem(14, new ItemBuilder(SkullCreator.itemFromBase64(installed.getSpawner().getType().getUrl()))
                    .setDisplayName("§eGerador de §f" + Utils.capitalizeFirstLetter(installed.getSpawner().getType().getName()))
                            .setLore(Arrays.asList("",
                            "§7Clique com §7§nesquerdo §7para resetar!"
                            ))
                    .build());

            inventory.setItem(16, new ItemBuilder(Material.CHEST).setDisplayName("§eArmazem").build());

        } else {

            inventory.setItem(11, new ItemBuilder(Material.NETHER_STAR).setDisplayName("§eInformações")
                            .setLore(infoLore)
                    .build());

            inventory.setItem(13, new ItemBuilder(SkullCreator.itemFromBase64(installed.getSpawner().getType().getUrl()))
                    .setDisplayName("§eGerador de §f" + Utils.capitalizeFirstLetter(installed.getSpawner().getType().getName()))
                    .build());


            inventory.setItem(15, new ItemBuilder(Material.CHEST).setDisplayName("§eArmazem").build());

        }

    }
}
