package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.parser.Entity;
import javax.swing.tree.TreeNode;
import java.math.BigDecimal;
import java.util.List;

public class TenmoService {

    private RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;
    private AuthenticatedUser currentUser;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public BigDecimal retrieveBalance() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Balance balance = restTemplate.exchange("http://localhost:8080/balance", HttpMethod.GET,
               entity, Balance.class).getBody();

        return balance.getBalance();
    }

    public Transfer sendTransfer(SentTransfer transfer) throws RestClientResponseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.authToken);
        HttpEntity<SentTransfer> entity = new HttpEntity<>(transfer, headers);

            ResponseEntity<Transfer> response = restTemplate.exchange("http://localhost:8080/send-transfer", HttpMethod.POST,
                    entity, Transfer.class);

            return response.getBody();

    }

    public User[] getAllUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<User[]> allUsers = restTemplate.exchange("http://localhost:8080/all-users", HttpMethod.GET,
                entity, User[].class);

        return allUsers.getBody();

    }

    public Transfer [] allTransfersByUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);


        try {
            ResponseEntity<Transfer[]> allTransfers = restTemplate.exchange("http://localhost:8080/transfers/", HttpMethod.GET,
                    entity, Transfer[].class);


            return allTransfers.getBody();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }



    public Transfer singleTransferById (int transferId) { //need to pass in transferId
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Transfer> singleTransfer = restTemplate.exchange("http://localhost:8080/transfers/" + transferId, HttpMethod.GET,
                entity, Transfer.class);

        System.out.println(singleTransfer.getBody());

        return singleTransfer.getBody();
    }


    //make entity methods??



}
