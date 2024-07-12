package com.daniel.spawners.inventories;

import com.daniel.spawners.Main;
import com.daniel.spawners.api.ItemBuilder;
import com.daniel.spawners.menu.Menu;
import com.daniel.spawners.model.SpawnerInstalled;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class StorageInventory extends Menu {

    private final static String LEVELUP_SOUND = Main.getVersion().value >= 13 ? "ENTITY_PLAYER_LEVELUP" : "LEVEL_UP";

    private final SpawnerInstalled installed;

    public StorageInventory(Player player, SpawnerInstalled installed) {
        super(player, "Armazem", 3*9);
        this.installed = installed;
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        ItemStack clicked = e.getCurrentItem();

        if (!clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String itemName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (itemName.equalsIgnoreCase("Voltar")) {

            player.openInventory(new MainInventory(player, installed).getInventory());

        }

        if (itemName.equalsIgnoreCase("Coletar")) {

            Inventory playerInventory = player.getInventory();
            List<ItemStack> storage = installed.getStorage();

            for (int i = 0; i < storage.size(); i++) {
                if (playerInventory.firstEmpty() == -1) {
                    player.sendMessage(Main.config().getString("Message.InventoryFull").replace('&', '§'));
                    player.playSound(player.getLocation(), Sound.CAT_MEOW, 2f, 2f);
                    break;
                }
                ItemStack nextItem = storage.get(i);
                if (nextItem != null && nextItem.getType() != Material.AIR) {
                    playerInventory.addItem(nextItem);
                    storage.remove(i);
                    i--;
                }
            }
            player.openInventory(getInventory());
            player.playSound(player.getLocation(), Sound.valueOf(LEVELUP_SOUND), 2f, 2f);

        }

    }

    @Override
    public void setItens(Inventory inventory) {

        inventory.setItem(18, new ItemBuilder(Material.ARROW).setDisplayName("§eVoltar").build());

        List<ItemStack> storage = installed.getStorage();

        if (storage.isEmpty()) {

            inventory.setItem(13, new ItemBuilder(Material.MINECART).setDisplayName("§eVazio").build());

        } else {

            inventory.setItem(13, new ItemBuilder(Material.STORAGE_MINECART).setDisplayName("§eColetar").build());

        }

    }
}
