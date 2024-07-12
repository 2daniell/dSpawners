package com.daniel.spawners.inventories;

import com.daniel.spawners.Main;
import com.daniel.spawners.api.ItemBuilder;
import com.daniel.spawners.api.SkullCreator;
import com.daniel.spawners.enums.Permission;
import com.daniel.spawners.menu.PaginatedMenu;
import com.daniel.spawners.model.SpawnerInstalled;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FriendsInventory extends PaginatedMenu {

    private static final int MAX_FRIENDS = Main.config().getInt("Max-Friends");
    public static final Map<UUID, SpawnerInstalled> inAdd = new HashMap<>();

    private final SpawnerInstalled installed;

    public FriendsInventory(Player player, SpawnerInstalled installed) {
        super(player, "Amigos", 4*9, MAX_FRIENDS);
        this.installed = installed;
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        ItemStack clicked = e.getCurrentItem();

        if (!clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String itemName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (page > 1) {
            if (itemName.equalsIgnoreCase("Voltar")) {
                page--;
                player.openInventory(getInventory());
            }
        } else if (subList(new ArrayList<>(installed.getTargetPermissions().keySet())).size() >= maxItensPerPage) {
            if (itemName.equalsIgnoreCase("Avançar")) {
                page++;
                player.openInventory(getInventory());
            }
        } if (itemName.equalsIgnoreCase("Opções de Navegação")) {

            if (e.getClick() == ClickType.LEFT) {

                if (installed.getTargetPermissions().size() < MAX_FRIENDS) {
                    player.closeInventory();
                    inAdd.put(player.getUniqueId(), installed);

                    Main.config().getStringList("Message.AddFriend").stream().map(msg -> msg.replace('&', '§')).forEach(player::sendMessage);
                } else {
                    player.sendMessage("§cO limite de amigos adicionados foi atingido.");
                }
            } else if (e.getClick() == ClickType.RIGHT) {

                player.openInventory(new MainInventory(player, installed).getInventory());

            }
        } else if (itemName.startsWith("Amigo")) {

            UUID id = UUID.fromString(ChatColor.stripColor(clicked.getItemMeta().getLore().get(0)));

            if (id.equals(player.getUniqueId())) {
                player.sendMessage("§cVocê não pode gerenciar você mesmo.");
                return;
            }

            if (e.getClick() == ClickType.LEFT) {

                player.openInventory(new PermissionInventory(player, installed, id).getInventory());

            } else if (e.getClick() == ClickType.RIGHT) {

                player.openInventory(new ConfirmInventory(player, installed, id).getInventory());

            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setItens(Inventory inventory) {

        inventory.setItem(30, new ItemBuilder(Material.ARROW).setDisplayName("§eVoltar").build());

        inventory.setItem(31, new ItemBuilder(SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2RhMjlmZTIzYjY5Zjc2YmM0OTQ3NDEwMjIyNmIwNjk5Y2EyYTVhZDExZjdhNDg1NDJiMTNhZDljYWJhZjg5ZCJ9fX0="))
                .setDisplayName("§eOpções de Navegação").setLore(Arrays.asList(
                        "",
                        "§7Clique com §7§nesquerdo §7para adicionar",
                        "§7Clique com §7§ndireito §7para voltar a pagina anterior"
                )).build());

        inventory.setItem(32, new ItemBuilder(Material.ARROW).setDisplayName("§eAvançar").build());

        if (installed.getTargetPermissions().isEmpty()) {

            inventory.setItem(13, new ItemBuilder(Material.WEB).setDisplayName("§eVazio").build());

        } else {

            List<UUID> subList = subList(new ArrayList<>(installed.getTargetPermissions().keySet()));

            int slot = 10;

            int i = 1;
            for (UUID id : subList) {

                if (slot == 17) slot = 19;

                OfflinePlayer target = Bukkit.getOfflinePlayer(id);

                List<String> lore = new ArrayList<>(Arrays.asList(
                        "§c" + target.getUniqueId(),
                        "",
                        "  §e" + target.getName(),
                        ""));
                lore.add("  §7Permissões:");

                Set<Permission> permissions = installed.getTargetPermissions().get(id);

                lore.add("");
                if (permissions.isEmpty()) {

                    lore.add("    §cSem permissões!");

                } else {
                    for (Permission permission : permissions) {
                        lore.add("    §7- §f" + permission.getName());
                    }
                }

                lore.add("");
                lore.add("§7Clique com §7§nesquerdo §7para gerenciar");
                lore.add("§7Clique com §7§ndireito §7para excluir");

                inventory.setItem(slot++, new ItemBuilder(SkullCreator.itemFromName(target.getName()))
                        .setDisplayName("§eAmigo #" + i++).setLore(lore).build());

            }
        }
    }
}
