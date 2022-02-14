package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.exceptions.SameUserException;
import com.techelevator.tenmo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AppController {

    @Autowired
    AccountDao accountDao;

    @Autowired
    UserDao userDao;

    @Autowired
    TransferDao transferDao;


    @RequestMapping(path ="/balance", method = RequestMethod.GET)
    public Balance obtainBalance(Principal principal) {

        String name = principal.getName();
        int userId = userDao.findIdByUsername(name);

        BigDecimal balance = accountDao.retrieveBalance(userId);

        Balance balanceObject = new Balance();
        balanceObject.setBalance(balance);

        return balanceObject;
    }

    //sendTransfer
    //int userFrom, int userTo, BigDecimal amount
    @RequestMapping(path = "/send-transfer", method = RequestMethod.POST)
    public void completeTransfer(@RequestBody SentTransfer transfer) throws SameUserException, InsufficientFundsException { //create another exception for insufficient funds, bad userID
        //need to assign variables in the new transfer
        //unsupported media type sending an object it wasn't expecting needed to send back request body

        int transferId = transferDao.sendTransfer(transfer);

    }


    //public List<Transfer> getAllTransfersByUser(int userId)
    //get all transfers

    @RequestMapping(path = "/transfers/", method = RequestMethod.GET)
    public List<Transfer> allTransfersByUser(Principal principal) {

        int userId = userDao.findIdByUsername(principal.getName());
        return transferDao.getAllTransfersByUser(userId);
    }


    //public Transfer getTransferById(int transactionId)
    //get transfers by id
    @RequestMapping(path = "/transfers/{transferId}", method = RequestMethod.GET)
    public Transfer singleTransferById(@PathVariable int transferId) {

        System.out.println(transferDao.getTransferById(transferId));
        return transferDao.getTransferById(transferId);
    }


    @RequestMapping(path = "/all-users", method = RequestMethod.GET)
    public List<User> allUsers (Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        List<User> allUsersArr = userDao.findAllWithoutHash(userId);
        return allUsersArr;
    }
}
