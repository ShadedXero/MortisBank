package com.mortisdevelopment.mortisbank.commands.subcommands.admin;

import com.mortisdevelopment.mortisbank.commands.CommandManager;
import com.mortisdevelopment.mortisbank.deposit.DepositManager;
import com.mortisdevelopment.mortisbank.personal.TransactionType;
import com.mortisdevelopment.mortisbank.utils.BankUtils;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DepositCommand extends PermissionCommand {

    private final DepositManager depositManager;

    public DepositCommand(CommandManager manager, DepositManager depositManager) {
        super("deposit", "mortisbank.admin.deposit", manager);
        this.depositManager = depositManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, String s, String[] args) {
//        if (args.length < 4) {
//            sender.sendMessage(getManager().getMessage("admin_deposit_usage"));
//            return false;
//        }
//        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
//        if (!target.hasPlayedBefore()) {
//            sender.sendMessage(getManager().getMessage("invalid_target"));
//            return false;
//        }
//        double amount;
//        try {
//            amount = Double.parseDouble(args[1]);
//        }catch (NumberFormatException exp) {
//            sender.sendMessage(getManager().getMessage("invalid_number"));
//            return false;
//        }
//        if (amount <= 0) {
//            sender.sendMessage(getManager().getMessage("invalid_number"));
//            return false;
//        }
//        if (!depositManager.deposit(target, amount)) {
//            sender.sendMessage(getManager().getMessage("could_not_process"));
//            return false;
//        }
//        return true;
//        BankUtils.getPlaceholder(amount).setPlaceholders(getManager().getSimpleMessage("admin_deposit"));
//        getManager().getPlaceholderMessage(target, "admin_deposit");
//        MessageUtils utils = new MessageUtils(manager.getPlaceholderMessage(target, "ADMIN_DEPOSIT"));
//        utils.replace("%amount%", formatter.format(amount));
//        utils.replace("%amount_raw%", String.valueOf(amount));
//        utils.replace("%amount_formatted%", MoneyUtils.getMoney(amount));
//        sender.sendMessage(utils.getMessage());
//        return true;
//        return checkTransaction(sender, args, TransactionType.DEPOSIT);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return null;
    }
}
