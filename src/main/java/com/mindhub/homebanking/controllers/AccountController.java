package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @RequestMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountService.getAccountsDTO();
    }

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        return accountService.getAccountDTO(id);
    }

    @RequestMapping(path = "/clients/current/accounts")
    public ResponseEntity<Object> getAccountsClientAuth(Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());

        List<AccountDTO> accountDTOS = client.getAccounts().stream().map(AccountDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(accountDTOS ,HttpStatus.OK);
    }

    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> register(Authentication authentication) {

        Client client = clientService.findByEmail(authentication.getName());
        Account account = null;

        do {
            int number1 = (int) ((Math.random() * (100000000 - 10000000)) + 10000000);
            String number = "VIN-" + number1;
            LocalDate creationDate = LocalDate.now();
            double balance = 0.0;
            account = new Account(number, creationDate, balance);
        } while (accountService.existsByNumber(account.getNumber()));



        if (client != null) {
            if (client.getAccounts().size() >= 3) {
                return new ResponseEntity<>("You have 3 or more accounts.", HttpStatus.FORBIDDEN);
            }
            client.addAccount(account);
            accountService.saveAccount(account);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}