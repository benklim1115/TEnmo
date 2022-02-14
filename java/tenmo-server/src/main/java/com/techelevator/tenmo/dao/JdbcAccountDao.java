package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //handles functionality with accounts
    //add, subtract & retrieve balance
    //get account by userId
    //get account by accountId

    @Override
    public BigDecimal retrieveBalance(int userId) {
        String sql = "SELECT balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);

        BigDecimal balance = null;

        if (results.next()) {
            balance = results.getBigDecimal("balance");
        }

        return balance;
    }

    @Override
    public BigDecimal subtractFromSenderBalance(int userId, BigDecimal amountToSubtractFrom) {
        BigDecimal amountFrom = retrieveBalance(userId);

        BigDecimal updatedBalance = amountFrom.subtract(amountToSubtractFrom);

        String sql = "UPDATE accounts SET balance = ? WHERE user_id = ?";

        try {
            jdbcTemplate.update(sql, updatedBalance, userId);
        } catch (Exception e){
            System.out.println(e.getMessage());
        };

        return updatedBalance;
    }

    @Override
    public BigDecimal addToRecipientBalance(int userId, BigDecimal amountToAddTo) {
        BigDecimal updatedBalance = retrieveBalance(userId).add(amountToAddTo);
        String sql = "UPDATE accounts SET balance = ? WHERE user_id = ?";

        try {
            jdbcTemplate.update(sql, updatedBalance, userId);
        } catch (Exception e){
            System.out.println(e.getMessage());
        };
        return updatedBalance;
    }

    //SqlRowSet result = template.queryForRowSet(getPuppySQL, id);

    @Override
    public Account getAccountByUserId(int userId) {
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        Account account = null;
        //map results into a new object

        if (results.next()) {
            account = mapResultsToAccount(results);
        }
        return account;
    }

    @Override
    public Account getAccountByAccountId(int accountId) {
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        Account account = null;
        //map results into a new object

        if (results.next()) {
            account = mapResultsToAccount(results);
        }

        return account;
    }

   //helper maprowtoset
    private Account mapResultsToAccount(SqlRowSet results) {
        Account account = new Account();
        account.setAccountId(results.getInt("account_id"));
        account.setUserId(results.getInt("user_id"));
        account.setBalance(results.getBigDecimal("balance"));

        return account;
    }

}
