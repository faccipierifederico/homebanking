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
    public ResponseEntity<Object> register(Authentication authentication, @RequestParam Double amount, @RequestParam String description,
                                           @RequestParam String accountOrigin, @RequestParam String accountDestination) {
        Client client = clientRepository.findByEmail(authentication.getName());

        // Verificar que exista la cuenta de origen
        if (!accountRepository.findbyNumber(accountOrigin).equals(accountOrigin)) {
            return new ResponseEntity<>("The account of origin does not exist.", HttpStatus.FORBIDDEN);
        }

        // Verificar que exista la cuenta de destino
        if (!accountRepository.findbyNumber(accountDestination).equals(accountDestination)) {
            return new ResponseEntity<>("The account of destination does not exist.", HttpStatus.FORBIDDEN);
        }

        // Verificar que cuenta Origen y Destino no sean iguales
        if (accountDestination.equals(accountOrigin)) {
            return new ResponseEntity<>("The account of Origin can not be the same as the account of destination.", HttpStatus.FORBIDDEN);
        }

        // Verificar que la cuenta de origen sea la del cliente autenticado. CHEQUEAR ESTO
        if (!accountOrigin.equals(client.getAccounts())) {
            return new ResponseEntity<>("The account of Origin must be the same as the Client who is logged in.", HttpStatus.FORBIDDEN);
        }
        // Verificar que la cuenta de origen tenga el monto disponible
        if (accountRepository.findbyNumber(accountOrigin).getBalance() < amount) {
            return new ResponseEntity<>("The balance of the account of origin must be higher than the amount of the transaction.", HttpStatus.FORBIDDEN);
        }
        // Verificar que el monto sea diferente y mayor a 0
        if (amount <= 0) {
            return new ResponseEntity<>("The amount must be higher than 0.", HttpStatus.FORBIDDEN);
        }
        // Verificar que la descripción sea completada
        if (description.isBlank()) {
            return new ResponseEntity<>("You must write down a description.", HttpStatus.FORBIDDEN);
        }
        // Verificar que la cuenta de destino no esté vacía
        if(accountDestination.isBlank()) {
            return new ResponseEntity<>("The account of destination can not be empty.", HttpStatus.FORBIDDEN);
        }
        // Verificar que la cuenta de origen no esté vacía
        if (accountOrigin.isBlank()) {
            return new ResponseEntity<>("The account of Origin can not be empty.", HttpStatus.FORBIDDEN);
        }

        // Crear las transacciones asociandolas a las cuentas correspondientes y por último guardarlas en el repositorio de transacciones
        LocalDateTime date = LocalDateTime.now();

        Transaction transactionDebit = new Transaction(TransactionType.DEBIT, (-amount), date, description);
        Transaction transactionCredit = new Transaction(TransactionType.CREDIT, amount, date, description);

        accountRepository.findbyNumber(accountOrigin).addTransaction(transactionDebit);
        accountRepository.findbyNumber(accountDestination).addTransaction(transactionCredit);
        transactionRepository.save(transactionCredit);
        transactionRepository.save(transactionDebit);

        // Debes actualizar cada cuenta con los montos correspondientes y guardarlas a través del repositorio de cuentas
        // Es decir, restar el monto a la cuenta de origen y sumarlo a la cuenta de destino

        double getBalanceOrigin = accountRepository.findbyNumber(accountOrigin).getBalance();
        double getBalanceDestination = accountRepository.findbyNumber(accountDestination).getBalance();

        // ¿cómo setear el nuevo balance en las cuentas?
        // No me sale ya que si uso el setBalance() me retorna un void y debería devolver un Double.

        accountRepository.findbyNumber(accountOrigin).setBalance(getBalanceOrigin - amount);
        accountRepository.findbyNumber(accountDestination).setBalance(getBalanceDestination + amount);

        // Account newAccountOrigin = new Account(accountOrigin, date, accountRepository.findbyNumber(accountOrigin).setBalance(getBalanceOrigin - amount));
        // Account newAccountDestination = new Account(accountDestination, date,
        //                                 accountRepository.findbyNumber(accountDestination).setBalance(getBalanceDestination + amount));

        // accountRepository.save(newAccountOrigin);
        // accountRepository.save(newAccountDestination);





        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}