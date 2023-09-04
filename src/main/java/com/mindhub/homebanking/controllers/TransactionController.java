package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Transactional
    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String fromAccountNumber, @RequestParam String toAccountNumber,
                                           @RequestParam Double amount, @RequestParam String description,
                                            Authentication authentication) {
        Client client = clientRepository.findByEmail(authentication.getName());
        Account accountOrigin = accountRepository.findByNumber(fromAccountNumber);
        Account accountDestination = accountRepository.findByNumber(toAccountNumber);


        // Verificar que exista la cuenta de origen
        if (!accountOrigin.getNumber().equals(fromAccountNumber)) {
            return new ResponseEntity<>("The account of origin does not exist.", HttpStatus.FORBIDDEN);
        }

        // Verificar que exista la cuenta de destino
        if (!accountDestination.getNumber().equals(toAccountNumber)) {
            return new ResponseEntity<>("The account of destination does not exist.", HttpStatus.FORBIDDEN);
        }

        // Verificar que cuenta Origen y Destino no sean iguales
        if (toAccountNumber.equals(fromAccountNumber)) {
            return new ResponseEntity<>("The account of origin can not be the same as the account of destination.", HttpStatus.FORBIDDEN);
        }

        // Verificar que la cuenta de origen sea la del cliente autenticado.
        if (!accountOrigin.getClient().equals(client)) {
            return new ResponseEntity<>("The account of origin must be the same as the client who is logged in.", HttpStatus.FORBIDDEN);
        }

        // Verificar que la cuenta de origen tenga el monto disponible
        if (accountOrigin.getBalance() < amount) {
            return new ResponseEntity<>("The balance of the account of origin must be higher than the amount of the transaction.", HttpStatus.FORBIDDEN);
        }
        // Verificar que el monto sea diferente y mayor a 0
        if (amount < 0) {
            return new ResponseEntity<>("The amount must be higher than 0.", HttpStatus.FORBIDDEN);
        }
        // Verificar que la descripción no esté vacía
        if (description.isBlank()) {
            return new ResponseEntity<>("You must write down a description.", HttpStatus.FORBIDDEN);
        }
        // Verificar que la cuenta de destino no esté vacía
        if(toAccountNumber.isBlank()) {
            return new ResponseEntity<>("The account of destination can not be empty.", HttpStatus.FORBIDDEN);
        }
        // Verificar que la cuenta de origen no esté vacía
        if (fromAccountNumber.isBlank()) {
            return new ResponseEntity<>("The account of origin can not be empty.", HttpStatus.FORBIDDEN);
        }

        // Crear las transacciones asociandolas a las cuentas correspondientes y por último guardarlas en el repositorio de transacciones
        LocalDateTime date = LocalDateTime.now();

        Transaction transactionDebit = new Transaction(TransactionType.DEBIT, amount, date, description);
        Transaction transactionCredit = new Transaction(TransactionType.CREDIT, amount, date, description);

        accountOrigin.addTransaction(transactionDebit);
        accountDestination.addTransaction(transactionCredit);
        transactionRepository.save(transactionCredit);
        transactionRepository.save(transactionDebit);

        // Debes actualizar cada cuenta con los montos correspondientes y guardarlas a través del repositorio de cuentas
        // Es decir, restar el monto a la cuenta de origen y sumarlo a la cuenta de destino

        accountOrigin.setBalance(accountOrigin.getBalance() - amount);
        accountDestination.setBalance(accountDestination.getBalance() + amount);

        accountRepository.save(accountOrigin);
        accountRepository.save(accountDestination);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}