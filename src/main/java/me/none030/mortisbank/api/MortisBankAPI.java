package me.none030.mortisbank.api;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MortisBankAPI{

    public double getBankBalance(UUID player);

    public int getBankAccount(UUID player);

    public double getPurse(UUID player);

    public LocalDateTime getInterestData(UUID player);

    public void StoreData(UUID player, double bankBalance, int bankAccount);

    public void ChangeBalance(UUID player, double bankBalance);

    public void ChangeAccount(UUID player, int bankAccount);

    public void ChangeInterest(UUID player, LocalDateTime time);

    public void GiveInterestMoney(String player, double amount);
}
