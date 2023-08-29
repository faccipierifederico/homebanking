package com.mindhub.homebanking.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TransactionController {


    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam Double amount, @RequestParam String description,
                                           @RequestParam String accountOrigin, @RequestParam String accountDestiny) {

        if (amount <= 0) {
            return new ResponseEntity<>("The amount must be higher than 0.", HttpStatus.FORBIDDEN);
        }
        if (description.isBlank()) {
            return new ResponseEntity<>("You must write down a description.", HttpStatus.FORBIDDEN);
        }

    return new ResponseEntity<>(HttpStatus.CREATED);
    }
}