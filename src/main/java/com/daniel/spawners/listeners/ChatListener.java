package com.daniel.spawners.listeners;

import com.daniel.spawners.Main;
import com.daniel.spawners.enums.Permission;
import com.daniel.spawners.inventories.FriendsInventory;
import com.daniel.spawners.model.SpawnerInstalled;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class ChatListener implements Listener {

    private final static String LEVELUP_SOUND = Main.getVersion().value >= 13 ? "ENTITY_PLAYER_LEVELUP" : "LEVEL_UP";

    private final Map<UUID, Pair<UUID, SpawnerInstalled>> pendingConfirmations = new HashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (FriendsInventory.inAdd.containsKey(playerUUID)) {
            e.setCancelled(true);
            handleAddFriend(player, e.getMessage());
        } else if (pendingConfirmations.containsKey(playerUUID)) {
            e.setCancelled(true);
            handleConfirmation(player, e.getMessage());
        }

    }

    private void handleAddFriend(Player player, String msg) {
        if (msg.length() < 3) {
            player.sendMessage("§cA palavra digitada é muito curta");
            return;
        }

        if (msg.equalsIgnoreCase("cancelar")) {
            player.playSound(player.getLocation(), Sound.valueOf(LEVELUP_SOUND), 2f, 2f);
            player.openInventory(new FriendsInventory(player, FriendsInventory.inAdd.get(player.getUniqueId())).getInventory());
            FriendsInventory.inAdd.remove(player.getUniqueId());
            return;
        }

        Player target = Bukkit.getPlayerExact(msg);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§cJogador inválido ou não está online");
            return;
        }

        SpawnerInstalled installed = FriendsInventory.inAdd.get(player.getUniqueId());
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("§cVocê não pode adicionar você mesmo");
            return;
        }

        if(installed.getOwner().equals(target.getUniqueId())) {
            player.sendMessage("§cO jogador já o dono do spawner.");
            return;
        }

        if (installed.getTargetPermissions().containsKey(target.getUniqueId())) {
            player.sendMessage("§cO jogador já está adicionado no spawner.");
            return;
        }


        pendingConfirmations.put(player.getUniqueId(), Pair.of(target.getUniqueId(), installed));
        FriendsInventory.inAdd.remove(player.getUniqueId());

        Main.config().getStringList("Message.FriendConfirm").stream().map($ -> $.replace('&', '§')
                        .replaceAll("%target%", Bukkit.getOfflinePlayer(target.getUniqueId()).getName()))
                .forEach(player::sendMessage);
    }

    private void handleConfirmation(Player player, String msg) {
        UUID targetUUID = pendingConfirmations.get(player.getUniqueId()).getLeft();

        SpawnerInstalled installed = pendingConfirmations.get(player.getUniqueId()).getRight();

        if (msg.equalsIgnoreCase("sim")) {

            installed.getTargetPermissions().put(targetUUID, new HashSet<>());
            player.openInventory(new FriendsInventory(player, installed).getInventory());

        } else if (msg.equalsIgnoreCase("não") || msg.equalsIgnoreCase("nao")) {

            player.openInventory(new FriendsInventory(player, installed).getInventory());

        } else {
            player.sendMessage("§cResposta inválida.");
            return;
        }

        pendingConfirmations.remove(player.getUniqueId());
    }
}
