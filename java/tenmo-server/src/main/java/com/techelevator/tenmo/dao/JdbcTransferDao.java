package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.exceptions.SameUserException;
import com.techelevator.tenmo.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;
    private Transfer transfer;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, AccountDao accountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
        this.transfer = new Transfer();
    }


    @Override
    public List<Transfer> getAllTransfersByUser(int userId) {
        List<Transfer> transfersByUser = new ArrayList<Transfer>();
        try {
            String sql = "SELECT t.transfer_id, transfer_type_desc, transfer_status_desc, t.account_from, t.account_to, t.amount, ua.username as username_from, ub as username_to " +
                    "FROM transfers as t " +

                    "JOIN accounts a ON t.account_from = a.account_id " +
                    "JOIN users ua on a.user_id = ua.user_id " +
                    "JOIN accounts b ON t.account_to = b.account_id " +
                    "JOIN users ub on b.user_id = ub.user_id " +

                    "JOIN transfer_types on transfer_types.transfer_type_id = t.transfer_type_id " +
                    "JOIN transfer_statuses on t.transfer_status_id = transfer_statuses.transfer_status_id " +

                    "WHERE a.user_id = ? OR b.user_id = ?";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);


            while (results.next()) {

                transfer = mapResultsToTransfer(results);
                transfersByUser.add(transfer);

            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return transfersByUser;
    }

    @Override
    public Transfer getTransferById(int transactionId) {
        String sql = "SELECT t.transfer_id, transfer_type_desc, transfer_status_desc, t.account_from, t.account_to, t.amount, ua.username as username_from, ub.username as username_to " +
                "FROM transfers as t " +

                "JOIN accounts a ON t.account_from = a.account_id " +
                "JOIN users ua on a.user_id = ua.user_id " +
                "JOIN accounts b ON t.account_to = b.account_id " +
                "JOIN users ub on b.user_id = ub.user_id " +

                "JOIN transfer_types on transfer_types.transfer_type_id = t.transfer_type_id " +
                "JOIN transfer_statuses on t.transfer_status_id = transfer_statuses.transfer_status_id " +
                "WHERE transfer_id = ?";


//                "SELECT transfer_id, transfer_type_desc, transfer_status_desc, account_from, account_to, amount " +
//                "FROM transfers " +
//                "JOIN transfer_types on transfer_types.transfer_type_id = transfers.transfer_type_id " +
//                "JOIN transfer_statuses on transfers.transfer_status_id = transfer_statuses.transfer_status_id " +
//                "WHERE transfer_id = ?";


        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transactionId);

        if(results.next()) {
            transfer = mapResultsToTransfer(results);
        }
        return transfer;
    }

    @Override
    public int sendTransfer(SentTransfer transfer) throws SameUserException, InsufficientFundsException {
        //logic for where transfer is going? Cannot send to ourselves?

        Account accountFrom = accountDao.getAccountByUserId(transfer.getUserFrom());

        Account accountTo = accountDao.getAccountByUserId(transfer.getUserTo());
        BigDecimal amount = transfer.getTransferAmount();
        System.out.println("userFrom: " + accountFrom.getUserId());
       // System.out.println("accountDao.retrieveBalance: " + accountDao.retrieveBalance(accountFrom.getUserId()));*/
        if (accountFrom.getAccountId() == accountTo.getAccountId()) {
            throw new SameUserException();

        } else if (accountFrom.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        //if(transfer amount < from balance && transfer amount > 0)
        else if (amount.compareTo(accountDao.retrieveBalance(accountFrom.getUserId())) == -1 && amount.compareTo(new BigDecimal(0)) == 1) {

            //TODO: list all users with user id

            String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES(2, 2, ?, ?, ?) RETURNING transfer_id"; //2 is type send, 2 is status approved

            int receivedId = jdbcTemplate.queryForObject(sql, Integer.class, accountFrom.getAccountId(), accountTo.getAccountId(), amount);
            //update sender account
            System.out.println(amount);
            accountDao.subtractFromSenderBalance(accountFrom.getUserId(), amount);
            //update recipient account
            accountDao.addToRecipientBalance(accountTo.getUserId(), amount);
            return receivedId;
        }
        return 0;
    }


    @Override
    public List<User> listUsersWithId() {

        return null;
    }


     private Transfer mapResultsToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();

        transfer.setTransferId(results.getInt("transfer_id")); //do we need this?
        transfer.setTransferTypeId(results.getString("transfer_type_desc"));
        transfer.setTransferStatusId(results.getString("transfer_status_desc"));
        transfer.setAccountFrom(results.getInt("account_from"));
        transfer.setAccountTo(results.getInt("account_to"));
        transfer.setTransferAmount(results.getBigDecimal("amount"));

        transfer.setUsernameTo(results.getString("username_to"));
        transfer.setUsernameFrom(results.getString("username_from"));


         return transfer;
    }


    //region notes
    //another helper maprowtoset
//    private Account mapResultsToAccount(SqlRowSet results) {
//        Account account = new Account();
//        account.setAccountId(results.getInt("account_id"));
//        account.setUserId(results.getInt("user_id"));
//        account.setBalance(results.getBigDecimal("balance"));
//
//        return account;
//    }


    //insert a new row in transfer table to document the transfer
    //update account balance of the sender
    //update account balance of the recipient
    //once the transfer has taken place...how do I get a list of transactions for either the sender OR recipient?
    //make an SQL statement "SELECT _ FROM transfers WHERE accountFrom = X or accountTo = X" x being the account id
    //how to transfer info(see notes) to client? make a separate class that just contains these "columns"
    //client will get this info back most likely as an array
    //loop through the array: if user ID is in the "to" property --> print out "From.... + amount"
    //if user ID is in the "from" prop. --> print out "To.... +  amount"


    //DAO organization
    //do not need one dao per table....think of dao's more like subject areas
    //account DAO (responsibilities = get balance, get your accountId given userId)
    //transfer DAO (responsibilities = make a transfer, audit and figure out who was involved in tranfer and see who was involved...see above)

    //bonus tasks:
    // update status of transfer in transfer DAO
    //endregion
}
