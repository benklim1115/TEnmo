package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TenmoService;
import com.techelevator.view.ConsoleService;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.*;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    //newly added option
    private static final String MAIN_MENU_OPTION_VIEW_TRANSFER_BY_ID = "View details of a specific transfer";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS,
            MAIN_MENU_OPTION_VIEW_TRANSFER_BY_ID, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private TenmoService tenmoService;

    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.tenmoService = new TenmoService();
    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to Tenmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_TRANSFER_BY_ID.equals(choice)) {
                viewTransferByTransferId();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private void viewCurrentBalance() {
        // TODO Auto-generated method stub
        System.out.println(tenmoService.retrieveBalance());

    }

    private void viewTransferHistory() {
        // TODO Auto-generated method stub

        try {
            System.out.println("-------------------------------------------\r\n" +
                    "Transfers\r\n" +
                    "ID\t\t\tFrom/To\t\t\t\tAmount\r\n" +
                    "-------------------------------------------\r\n");

            Transfer[] allTransfers = tenmoService.allTransfersByUser();

            String toOrFrom = "";
            String userName = "";
            assert false;
            for (Transfer transfer : allTransfers) {
                if (currentUser.getUser().getId() == transfer.getAccountFrom()) {
                    toOrFrom = "To: ";
                    userName = transfer.getUsernameTo();
                } else {
                    toOrFrom = "From: ";
                    userName = transfer.getUsernameFrom();
                }

                System.out.println(transfer.getTransferId() + "\t" + toOrFrom + userName + "\t\t\t\t$" + transfer.getTransferAmount());
            }

            System.out.print("-------------------------------------------\r\n" +
                    "Please enter transfer ID to view details (0 to cancel): \n");

            //if user enters 0, need to exit
            Scanner scanner = new Scanner(System.in);
            String inputString = scanner.nextLine();
            int inputInt = Integer.parseInt(inputString);

            if(inputInt == 0){
                mainMenu();
            } else {
                viewTransferByTransferId();
            }


        } catch (Exception e) {
            e.getMessage();
            System.out.println("Something went wrong");
        }


        Transfer[] allTransfers = tenmoService.allTransfersByUser();

        System.out.println("-------------------------------------------");

    }

    //newly added method in main menu
    private void viewTransferByTransferId() {
        // TODO Auto-generated method stub
        //interface from readme

        //Transfer transfer = tenmoService.singleTransferById(transferId);
        Transfer transfer = tenmoService.singleTransferById(console.getUserInputInteger("Enter the transfer ID: "));

        System.out.println("--------------------------------------------\n" +
                "Transfer Details\n" +
                "--------------------------------------------");

        System.out.println("ID: " + transfer.getTransferId());
        System.out.println("From: " + transfer.getUsernameFrom());
        System.out.println("To: " + transfer.getUsernameTo()); //prints out entire object -->this is whats on user table in db
        System.out.println("Type: " + transfer.getTransferTypeId()); //null?
        System.out.println("Status: " + transfer.getTransferStatusId()); //null?
        System.out.println("Amount: $" + transfer.getTransferAmount());


        System.out.println("-------------------------------------------");

    }

    private void sendBucks() {
        // TODO Auto-generated method stub
        //1. get list of user id and usernames tenmoservice.getAllUsers();
        List<User> allUsers = Arrays.asList(tenmoService.getAllUsers().clone());
        SentTransfer transfer = new SentTransfer();
        // \t for tabbing, \r carriage return, \n for new line
        System.out.println("-------------------------------------------\r\n" +
                "Users\r\n" +
                "ID\t\t\tName\r\n" +
                "-------------------------------------------");
        for (User user : allUsers) {
            System.out.println(user.getId() + "     " + user.getUsername());
        }
        System.out.println("-------------------------------------------");

        //2. prompt user for recipient
        System.out.print("Which user ID do you want to send money to: ");
        Scanner userInput = new Scanner(System.in);
        String userTo = userInput.nextLine();

        System.out.println("-------------------------------------------");

        //3. prompt for amount
        System.out.println("Enter an amount you wish to send: ");
        String amountStr = userInput.nextLine();
        BigDecimal parsedAmount = new BigDecimal(amountStr);
        System.out.println("-------------------------------------------");

        //4. Set userFrom, userTo, amount in transfer object
        transfer.setUserFrom(currentUser.getUser().getId());
        transfer.setUserTo(Integer.parseInt(userTo));
        transfer.setTransferType("Send");
        try {
            transfer.setTransferAmount(parsedAmount);
            System.out.println("Transferring " + parsedAmount);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a correct amount to send!");
        }

        //5. Send transfer
        try {
            tenmoService.sendTransfer(transfer);
        } catch (RestClientResponseException e) {
            System.out.println("Perhaps you do not have enough money in your account. Try again.");
        }


    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

    private void requestBucks() {
        // TODO Auto-generated method stub

    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
                // TODO: set the token on the Tenmo service
                tenmoService.setAuthToken(currentUser.getToken());
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
