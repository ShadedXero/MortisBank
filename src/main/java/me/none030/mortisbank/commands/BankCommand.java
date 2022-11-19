package me.none030.mortisbank.commands;

import me.none030.mortisbank.inventories.PersonalBankInventory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.none030.mortisbank.methods.DataMethods.*;
import static me.none030.mortisbank.methods.Depositing.onDeposit;
import static me.none030.mortisbank.methods.Interests.getInterest;
import static me.none030.mortisbank.methods.ReloadingMethod.Reload;
import static me.none030.mortisbank.methods.Upgrades.getLimit;
import static me.none030.mortisbank.methods.Upgrades.getMaxAccount;
import static me.none030.mortisbank.methods.Withdrawing.onWithdraw;

public class BankCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                System.out.println("Usage: /bank open <player_name>");
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    System.out.println("Mortis Bank");
                    System.out.println("/bank help - Shows the commands");
                    System.out.println("/bank - Opens the bank");
                    System.out.println("/bank open <player_name> - Opens the bank for the specified player");
                    System.out.println("/bank admin - Shows the admin commands");
                    return true;
                }
                if (args[0].equalsIgnoreCase("admin")) {
                    System.out.println("Mortis Bank");
                    System.out.println("/bank admin - Shows the admin commands");
                    System.out.println("/bank admin reload - Reloads the plugin");
                    System.out.println("/bank admin balance set <player_name|uuid> <balance> - Sets the player's bank balance");
                    System.out.println("/bank admin balance add <player_name|uuid> <balance> - Adds the amount to the player's bank balance");
                    System.out.println("/bank admin balance remove <player_name|uuid> <balance> - Removes the amount from the player's bank balance");
                    System.out.println("/bank admin account set <player_name|uuid> <account-id> - Sets the account of the player");
                    System.out.println("/bank admin interest give <player_name|uuid> - Gives interest to the player");
                    System.out.println("/bank admin deposit <player_name|uuid> <amount> - Make the player deposit the amount of money");
                    System.out.println("/bank admin withdraw <player_name|uuid> <amount> - Make the player withdraw the amount of money");
                    return true;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("reload")) {
                    Reload();
                    System.out.println("Reloaded");
                    return true;
                }
                if (args[0].equalsIgnoreCase("open")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        PersonalBankInventory inv = new PersonalBankInventory();
                        target.openInventory(inv.getInventory());
                        return true;
                    } else {
                        System.out.println("Invalid Player");
                    }
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("interest") && args[2].equalsIgnoreCase("give")) {
                    Player target = Bukkit.getPlayer(args[3]);
                    double balance;
                    int account;
                    if (target != null) {
                        balance = getBankBalance(target.getUniqueId());
                        account = getBankAccount(target.getUniqueId());
                        double amount = getInterest(balance, account);
                        GiveBankInterestMoney(target.getName(), amount);
                        System.out.println("Admin Command Executed");
                    } else {
                        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                        if (offlineTarget != null) {
                            balance = getBankBalance(offlineTarget.getUniqueId());
                            account = getBankAccount(offlineTarget.getUniqueId());
                            double amount = getInterest(balance, account);
                            GiveBankInterestMoney(offlineTarget.getName(), amount);
                            System.out.println("Admin Command Executed");
                        } else {
                            System.out.println("Invalid Player");
                        }
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("deposit")) {
                    Player target = Bukkit.getPlayer(args[2]);
                    double amount = Double.parseDouble(args[3]);
                    if (target != null) {
                        onDeposit(target, amount);
                        System.out.println("Admin Command Executed");
                    } else {
                        System.out.println("Invalid Player");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("withdraw")) {
                    Player target = Bukkit.getPlayer(args[2]);
                    double amount = Double.parseDouble(args[3]);
                    if (target != null) {
                        onWithdraw(target, amount);
                        System.out.println("Admin Command Executed");
                    } else {
                        System.out.println("Invalid Player");
                    }
                    return true;
                }
            }
            if (args.length == 5) {
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("balance") && args[2].equalsIgnoreCase("set")) {
                    Player target = Bukkit.getPlayer(args[3]);
                    double amount = Double.parseDouble(args[4]);
                    if (target != null) {
                        ChangeBankBalance(target.getUniqueId(), amount);
                        System.out.println("Admin Command Executed");
                    } else {
                        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                        if (offlineTarget != null) {
                            ChangeBankBalance(offlineTarget.getUniqueId(), amount);
                            System.out.println("Admin Command Executed");
                        } else {
                            System.out.println("Invalid Player");
                        }
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("balance") && args[2].equalsIgnoreCase("add")) {
                    Player target = Bukkit.getPlayer(args[3]);
                    double amount = Double.parseDouble(args[4]);
                    double balance;
                    int account;
                    double limit;
                    if (target != null) {
                        balance = getBankBalance(target.getUniqueId());
                        account = getBankAccount(target.getUniqueId());
                        limit = getLimit(account);
                        if (balance + amount <= limit) {
                            ChangeBankBalance(target.getUniqueId(), balance + amount);
                            System.out.println("Admin Command Executed");
                        } else {
                            System.out.println("Could not process this command.");
                        }
                    } else {
                        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                        if (offlineTarget != null) {
                            balance = getBankBalance(offlineTarget.getUniqueId());
                            account = getBankAccount(offlineTarget.getUniqueId());
                            limit = getLimit(account);
                            if (balance + amount <= limit) {
                                ChangeBankBalance(offlineTarget.getUniqueId(), balance + amount);
                                System.out.println("Admin Command Executed");
                            } else {
                                System.out.println("Could not process this command.");
                            }
                        } else {
                            System.out.println("Invalid Player");
                        }
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("balance") && args[2].equalsIgnoreCase("remove")) {
                    Player target = Bukkit.getPlayer(args[3]);
                    double amount = Double.parseDouble(args[4]);
                    double balance;
                    if (target != null) {
                        balance = getBankBalance(target.getUniqueId());
                        if (amount <= balance) {
                            ChangeBankBalance(target.getUniqueId(), balance - amount);
                        } else {
                            ChangeBankBalance(target.getUniqueId(), 0);
                        }
                        System.out.println("Admin Command Executed");
                    } else {
                        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                        if (offlineTarget != null) {
                            balance = getBankBalance(offlineTarget.getUniqueId());
                            if (amount <= balance) {
                                ChangeBankBalance(offlineTarget.getUniqueId(), balance - amount);
                            } else {
                                ChangeBankBalance(offlineTarget.getUniqueId(), 0);
                            }
                            System.out.println("Admin Command Executed");
                        } else {
                            System.out.println("Invalid Player");
                        }
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("account") && args[2].equalsIgnoreCase("set")) {
                    Player target = Bukkit.getPlayer(args[3]);
                    int account = Integer.parseInt(args[4]);
                    if (account <= getMaxAccount()) {
                        if (target != null) {
                            ChangeBankAccount(target.getUniqueId(), account);
                            System.out.println("Admin Command Executed");
                        } else {
                            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                            if (offlineTarget != null) {
                                ChangeBankAccount(offlineTarget.getUniqueId(), account);
                                System.out.println("Admin Command Executed");
                            } else {
                                System.out.println("Invalid Player");
                            }
                        }
                        return true;
                    } else {
                        System.out.println("Could not process this command.");
                    }
                }
            }
        } else {
            Player player = (Player) sender;

            if (args.length == 0) {
                if (player.hasPermission("bank.main")) {
                    PersonalBankInventory inv = new PersonalBankInventory();
                    player.openInventory(inv.getInventory());
                    return true;
                } else {
                    player.sendMessage("§cYou don't have permission to use this");
                }
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    if (player.hasPermission("bank.help")) {
                        player.sendMessage("§eMortis Bank");
                        player.sendMessage("§e/bank help §7- Shows the commands");
                        player.sendMessage("§e/bank §7- Opens the bank");
                        player.sendMessage("§e/bank open <player_name> §7- Opens the bank for the specified player");
                        player.sendMessage("§e/bank admin §7- Shows the admin commands");
                        return true;
                    } else {
                        player.sendMessage("§cYou don't have permission to use this");
                    }
                }
                if (args[0].equalsIgnoreCase("admin")) {
                    if (player.hasPermission("bank.admin")) {
                        player.sendMessage("§eMortis Bank");
                        player.sendMessage("§e/bank admin §7- Shows the admin commands");
                        player.sendMessage("§e/bank admin reload §7- Reloads the plugin");
                        player.sendMessage("§e/bank admin balance set <player_name|uuid> <balance> §7- Sets the player's bank balance");
                        player.sendMessage("§e/bank admin balance add <player_name|uuid> <balance> §7- Adds the amount to the player's bank balance");
                        player.sendMessage("§e/bank admin balance remove <player_name|uuid> <balance> §7- Removes the amount from the player's bank balance");
                        player.sendMessage("§e/bank admin account set <player_name|uuid> <account-id> §7- Sets the account of the player");
                        player.sendMessage("§e/bank admin interest give <player_name|uuid> §7- Gives interest to the player");
                        player.sendMessage("§e/bank admin deposit <player_name|uuid> <amount> §7- Make the player deposit the amount of money");
                        player.sendMessage("§e/bank admin withdraw <player_name|uuid> <amount> §7- Make the player withdraw the amount of money");
                        return true;
                    } else {
                        player.sendMessage("§cYou don't have permission to use this");
                    }
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("bank.admin")) {
                        Reload();
                        player.sendMessage("§aReloaded");
                        return true;
                    } else {
                        player.sendMessage("§cYou don't have permission to use this");
                    }
                }
                if (args[0].equalsIgnoreCase("open")) {
                    if (player.hasPermission("bank.open")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            PersonalBankInventory inv = new PersonalBankInventory();
                            target.openInventory(inv.getInventory());
                            return true;
                        } else {
                            player.sendMessage("§cInvalid Player");
                        }
                    } else {
                        player.sendMessage("§cYou don't have permission to use this");
                    }
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("interest") && args[2].equalsIgnoreCase("give")) {
                    if (player.hasPermission("bank.admin")) {
                        Player target = Bukkit.getPlayer(args[3]);
                        double balance;
                        int account;
                        if (target != null) {
                            balance = getBankBalance(target.getUniqueId());
                            account = getBankAccount(target.getUniqueId());
                            double amount = getInterest(balance, account);
                            GiveBankInterestMoney(target.getName(), amount);
                            player.sendMessage("§aAdmin Command Executed");
                        } else {
                            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                            if (offlineTarget != null) {
                                balance = getBankBalance(offlineTarget.getUniqueId());
                                account = getBankAccount(offlineTarget.getUniqueId());
                                double amount = getInterest(balance, account);
                                GiveBankInterestMoney(offlineTarget.getName(), amount);
                                player.sendMessage("§aAdmin Command Executed");
                            } else {
                                player.sendMessage("§cInvalid Player");
                            }
                        }
                        return true;
                    } else {
                        player.sendMessage("§cYou don't have permission to use this");
                    }
                }
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("deposit")) {
                    if (player.hasPermission("bank.admin")) {
                        Player target = Bukkit.getPlayer(args[2]);
                        double amount = Double.parseDouble(args[3]);
                        if (target != null) {
                            onDeposit(target, amount);
                            player.sendMessage("§aAdmin Command Executed");
                        } else {
                            player.sendMessage("§cInvalid Player");
                        }
                        return true;
                    } else {
                        player.sendMessage("§cYou don't have permission to use this");
                    }
                }
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("withdraw")) {
                    if (player.hasPermission("bank.admin")) {
                        Player target = Bukkit.getPlayer(args[2]);
                        double amount = Double.parseDouble(args[3]);
                        if (target != null) {
                            onWithdraw(target, amount);
                            player.sendMessage("§aAdmin Command Executed");
                        } else {
                            player.sendMessage("§cInvalid Player");
                        }
                        return true;
                    } else {
                        player.sendMessage("§cYou don't have permission to use this");
                    }
                }
            }
            if (args.length == 5) {
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("balance") && args[2].equalsIgnoreCase("set")) {
                    if (player.hasPermission("bank.admin")) {
                        Player target = Bukkit.getPlayer(args[3]);
                        double amount = Double.parseDouble(args[4]);
                        if (target != null) {
                            ChangeBankBalance(target.getUniqueId(), amount);
                            player.sendMessage("§aAdmin Command Executed");
                        } else {
                            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                            if (offlineTarget != null) {
                                ChangeBankBalance(offlineTarget.getUniqueId(), amount);
                                player.sendMessage("§aAdmin Command Executed");
                            } else {
                                player.sendMessage("§cInvalid Player");
                            }
                        }
                        return true;
                    } else {
                        player.sendMessage("§cYou don't have permission to use this");
                    }
                }
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("balance") && args[2].equalsIgnoreCase("add")) {
                    if (player.hasPermission("bank.admin")) {
                        Player target = Bukkit.getPlayer(args[3]);
                        double amount = Double.parseDouble(args[4]);
                        double balance;
                        int account;
                        double limit;
                        if (target != null) {
                            balance = getBankBalance(target.getUniqueId());
                            account = getBankAccount(target.getUniqueId());
                            limit = getLimit(account);
                            double spaceInBank = limit - balance;
                            if (balance + amount < spaceInBank) {
                                ChangeBankBalance(target.getUniqueId(), balance + amount);
                                player.sendMessage("§aAdmin Command Executed");
                            } else {
                                player.sendMessage("§cCould not process this command.");
                            }
                        } else {
                            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                            if (offlineTarget != null) {
                                balance = getBankBalance(offlineTarget.getUniqueId());
                                account = getBankAccount(offlineTarget.getUniqueId());
                                limit = getLimit(account);
                                double spaceInBank = limit - balance;
                                if (balance + amount < spaceInBank) {
                                    ChangeBankBalance(offlineTarget.getUniqueId(), balance + amount);
                                    player.sendMessage("§aAdmin Command Executed");
                                } else {
                                    player.sendMessage("§cCould not process this command.");
                                }
                            } else {
                                player.sendMessage("§cInvalid Player");
                            }
                        }
                        return true;
                    } else {
                        player.sendMessage("§cYou don't have permission to use this");
                    }
                }
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("balance") && args[2].equalsIgnoreCase("remove")) {
                    if (player.hasPermission("bank.admin")) {
                        Player target = Bukkit.getPlayer(args[3]);
                        double amount = Double.parseDouble(args[4]);
                        double balance;
                        if (target != null) {
                            balance = getBankBalance(target.getUniqueId());
                            if (amount <= balance) {
                                ChangeBankBalance(target.getUniqueId(), balance - amount);
                                player.sendMessage("§aAdmin Command Executed");
                            } else {
                                ChangeBankBalance(target.getUniqueId(), 0);
                                player.sendMessage("§aAdmin Command Executed");
                            }
                        } else {
                            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                            if (offlineTarget != null) {
                                balance = getBankBalance(offlineTarget.getUniqueId());
                                if (amount <= balance) {
                                    ChangeBankBalance(offlineTarget.getUniqueId(), balance - amount);
                                    player.sendMessage("§aAdmin Command Executed");
                                } else {
                                    ChangeBankBalance(offlineTarget.getUniqueId(), 0);
                                    player.sendMessage("§aAdmin Command Executed");
                                }
                            } else {
                                player.sendMessage("§cInvalid Player");
                            }
                        }
                        return true;
                    } else {
                        player.sendMessage("§cYou don't have permission to use this");
                    }
                }
                if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("account") && args[2].equalsIgnoreCase("set")) {
                    if (player.hasPermission("bank.admin")) {
                        Player target = Bukkit.getPlayer(args[3]);
                        int account = Integer.parseInt(args[4]);
                        if (account <= getMaxAccount()) {
                            if (target != null) {
                                ChangeBankAccount(target.getUniqueId(), account);
                                player.sendMessage("§aAdmin Command Executed");
                            } else {
                                OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                                if (offlineTarget != null) {
                                    ChangeBankAccount(offlineTarget.getUniqueId(), account);
                                    player.sendMessage("§aAdmin Command Executed");
                                } else {
                                    player.sendMessage("§cInvalid Player");
                                }
                            }
                            return true;
                        } else  {
                            player.sendMessage("§cCould not process this command.");
                        }
                    } else {
                        player.sendMessage("§cYou don't have permission to use this");
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("help");
            if (sender.hasPermission("bank.open")) {
                arguments.add("open");
            }
            if (sender.hasPermission("bank.admin")) {
                arguments.add("admin");
            }
            return arguments;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("admin")) {
                List<String> arguments = new ArrayList<>();
                if (sender.hasPermission("bank.admin")) {
                    arguments.add("reload");
                    arguments.add("balance");
                    arguments.add("account");
                    arguments.add("interest");
                    arguments.add("deposit");
                    arguments.add("withdraw");
                }
                return arguments;
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("admin")) {
                if (args[1].equalsIgnoreCase("balance")) {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("set");
                    arguments.add("add");
                    arguments.add("remove");
                    return arguments;
                }
                if (args[1].equalsIgnoreCase("account")) {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("set");
                    return arguments;
                }
                if (args[1].equalsIgnoreCase("interest")) {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("give");
                    return arguments;
                }
            }
        }

        return null;
    }
}
