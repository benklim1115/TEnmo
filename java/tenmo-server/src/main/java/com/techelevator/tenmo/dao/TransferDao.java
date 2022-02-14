package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.exceptions.SameUserException;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.SentTransfer;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    public List<Transfer> getAllTransfersByUser(int userId);
    public Transfer getTransferById(int transactionId);
    public int sendTransfer(SentTransfer transfer) throws SameUserException, InsufficientFundsException;
    List<User> listUsersWithId();

    //getTransferInfo()
    //updateSender()
    //updateRecipient()

//    public String requestTransfer(int userFrom, int userTo, BigDecimal amount);
//    public List<Transfers> getPendingRequests(int userId);
//    public String updateTransferRequest(Transfers transfer, int statusId);


}
