package me.none030.mortisbank.commands;

import me.none030.mortisbank.inventories.PersonalBankInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class BankCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                System.out.println("Usage: /bank open <player_name>");
                return true;
            }
            if (args[0].equalsIgnoreCase("open")) {
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        PersonalBankInventory inv = new PersonalBankInventory();
                        target.openInventory(inv.getInventory());
                        return true;
                    } else {
                        System.out.println("Invalid Player");
                    }
                } else {
                    System.out.println("Usage: /bank open <player_name>");
                }
            }
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("bank.main")) {
                if (args.length == 0) {
                    PersonalBankInventory inv = new PersonalBankInventory();
                    player.openInventory(inv.getInventory());
                    return true;
                }
            } else {
                player.sendMessage("§cYou don't have permission to use this");
            }
            if (player.hasPermission("bank.open")) {
                if (args[0].equalsIgnoreCase("open")) {
                    if (args.length == 2) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            PersonalBankInventory inv = new PersonalBankInventory();
                            target.openInventory(inv.getInventory());
                            return true;
                        } else {
                            player.sendMessage("§cInvalid Player");
                        }
                    } else {
                        player.sendMessage("§cUsage: /bank open <player_name>");
                    }
                }
            } else {
                player.sendMessage("§cYou don't have permission to use this");
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            if (sender.hasPermission("bank.open")) {
                return Collections.singletonList("open");
            }
        }

        return null;
    }
}
