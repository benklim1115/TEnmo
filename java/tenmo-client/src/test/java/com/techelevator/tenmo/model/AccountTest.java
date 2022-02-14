package com.techelevator.tenmo.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AccountTest {

    @Test
    public void canMakeAccountObject() {

        Account testAccount = new Account();
        testAccount.setBalance(new BigDecimal("100.00"));
        testAccount.setAccountId(1001);
        testAccount.setUserId(3001);

        BigDecimal actualBalance = testAccount.getBalance();
        assertEquals(new BigDecimal("100.00"), actualBalance);
    }

}