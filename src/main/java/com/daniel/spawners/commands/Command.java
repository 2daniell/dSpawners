package com.daniel.spawners.commands;

import com.daniel.spawners.Main;
import com.daniel.spawners.handler.SpawnerHandler;
import com.daniel.spawners.model.Spawner;
import com.daniel.spawners.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (sender.hasPermission("spawners.give")) {
            if(args.length == 0) {
                sender.sendMessage(Main.config().getString("Message.Usage").replace('&', '§'));
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                Main.reloadConfigs();
                sender.sendMessage(Main.config().getString("Message.ReloadConfigs").replace('&', '§'));
                return true;
            } else if(args[0].equalsIgnoreCase("give")) {

                if (args.length < 2) {
                    sender.sendMessage(Main.config().getString("Message.Usage").replace('&', '§'));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage(Main.config().getString("Message.InvalidTarget").replace('&', '§'));
                    return true;
                }

                if (args.length < 3) {
                    sender.sendMessage(Main.config().getString("Message.Usage").replace('&', '§'));
                    return true;
                }

                Spawner spawner = SpawnerHandler.findByName(args[2]);
                if (spawner == null) {
                    sender.sendMessage(Main.config().getString("Message.SpawnerInvalid").replace('&', '§'));
                    return true;
                }

                if (target.getInventory().firstEmpty() >= 0) {
                    target.getInventory().addItem(spawner.getItem());
                    sender.sendMessage(Main.config().getString("Message.SpawnerGive").replace('&', '§')
                            .replaceAll("%target%", target.getName()).replaceAll("%type%", Utils.capitalizeFirstLetter(spawner.getType().getName())));
                } else {
                    sender.sendMessage(Main.config().getString("Message.InventoryFull").replace('&', '§'));
                }
                return true;

            } else {
                sender.sendMessage(Main.config().getString("Message.Usage").replace('&', '§'));
                return true;
            }

        } else {
            sender.sendMessage(Main.config().getString("Message.NoPerm").replace('&', '§'));
        }

        return false;
    }
}
