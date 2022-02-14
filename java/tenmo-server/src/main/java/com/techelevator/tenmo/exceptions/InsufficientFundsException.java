package com.techelevator.tenmo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "You cannot transfer more money than you have in your account.")
public class InsufficientFundsException extends Exception{


    public InsufficientFundsException () {
        super("You cannot transfer more money than you have in your account.");
    }
}
