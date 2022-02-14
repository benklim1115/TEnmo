package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    public BigDecimal retrieveBalance(int userId);
    public BigDecimal subtractFromSenderBalance(int userId, BigDecimal amountToSubtract);
    public BigDecimal addToRecipientBalance(int userId, BigDecimal amountToAdd);
    public Account getAccountByUserId(int userId);
    public Account getAccountByAccountId(int accountId);

    //still need functionality to get accounts
    //account getbyUserId
    //account getbyAccountId?

}
