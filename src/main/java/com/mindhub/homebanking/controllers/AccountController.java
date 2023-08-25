package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
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
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(Collectors.toList());
    }

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }

    // lo nuevo..

    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> register(Authentication authentication) {
        Client client = clientRepository.findByEmail(authentication.getName());

        int number1 = (int) ((Math.random() * (100000000 - 10000000)) + 10000000);
        String number = "VIN-" + number1;
        LocalDate creationDate = LocalDate.now();
        double balance = 0.0;
        Account account = new Account(number, creationDate, balance);

        if (client != null) {
            if (client.getAccounts().size() >= 3) {
                return new ResponseEntity<>("You have 3 or more accounts.", HttpStatus.FORBIDDEN);
            }
            client.addAccount(account);

            accountRepository.save(account);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

}