package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ClientLoanRepository clientLoanRepository;


    @RequestMapping(path = "/loans")
    public ResponseEntity<Object> getLoans() {
        List<LoanDTO> loanDTOS = loanRepository.findAll().stream().map(LoanDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(loanDTOS, HttpStatus.OK);
    }


    @Transactional
    @RequestMapping(path = "/loans", method = RequestMethod.POST)
    public ResponseEntity<Object> addLoan(Authentication authentication, @RequestBody LoanApplicationDTO loanApplication) {
        Client client = clientRepository.findByEmail(authentication.getName());
        Account accountDestination = accountRepository.findByNumber(loanApplication.getToAccountNumber());

        System.out.println(loanApplication.getLoanId());

        if (loanApplication.getLoanId() == null || loanApplication.getLoanId() <= 0) {
            return new ResponseEntity<>("The ID can not be null and must not be 0", HttpStatus.FORBIDDEN);
        }

        Loan loan = loanRepository.findById(loanApplication.getLoanId()).orElse(null);


        // Loan diferente a nulo
        if (loanApplication == null){
            return new ResponseEntity<>("The loan application is null.", HttpStatus.FORBIDDEN);
        }

        // El monto debe ser igual o mayor a cero
        if (loanApplication.getAmount() <= 0) {
            return new ResponseEntity<>("The amount of the loan must be higher than 0.", HttpStatus.FORBIDDEN);
        }

        // Los pagos deben ser mayor a cero
        if (loanApplication.getPayments() <= 0) {
            return new ResponseEntity<>("The payments of the loan must be higher than 0.", HttpStatus.FORBIDDEN);
        }

        if (loanApplication.getAmount() > loan.getMaxAmount()) {
            return new ResponseEntity<>("The max amount of the loan is " + loan.getMaxAmount(), HttpStatus.FORBIDDEN);
        }

        if (!loan.getPayments().contains(loanApplication.getPayments())) {
            return new ResponseEntity<>("The amount of payments is incorrect", HttpStatus.FORBIDDEN);
        }

/*        // El monto del préstamo y la cantidad de cuotas no puede superar el monto máximo a pedir o la cantidad de cuotas disponibles.
        if (loanApplication.getId() == 1) {
            if (loanApplication.getAmount() > 500000) {
                return new ResponseEntity<>("The max amount of the loan is 500000.", HttpStatus.FORBIDDEN);
            }
            if (loanApplication.getPayments() != 12 && loanApplication.getPayments() != 24 && loanApplication.getPayments() != 36
                    && loanApplication.getPayments() != 48 && loanApplication.getPayments() != 60) {
                return new ResponseEntity<>("The payments must be on the following options: 12, 24, 36, 48, 60.", HttpStatus.FORBIDDEN);
            }
        } else if (loanApplication.getId() == 2) {
            if (loanApplication.getAmount() >= 100000) {
                return new ResponseEntity<>("The max amount of the loan is 100000.", HttpStatus.FORBIDDEN);
            }
            if (loanApplication.getPayments() != 6 && loanApplication.getPayments() != 12 && loanApplication.getPayments() != 24) {
                return new ResponseEntity<>("The payments must be on the following options: 6, 12, 24.", HttpStatus.FORBIDDEN);
            }
        } else if (loanApplication.getId() == 3) {
            if (loanApplication.getAmount() >= 300000) {
                return new ResponseEntity<>("The max amount of the loan is 300000.", HttpStatus.FORBIDDEN);
            }
            if (loanApplication.getPayments() != 6 && loanApplication.getPayments() != 12 && loanApplication.getPayments() != 24
            && loanApplication.getPayments() != 36) {
                return new ResponseEntity<>("The payments must be on the following options: 6, 12, 24, 36", HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>("The loan you asked for, do not exist.", HttpStatus.FORBIDDEN);
        }*/

        // Verificar que la cuenta de destino exista
        if (!loanApplication.getToAccountNumber().equals(accountDestination.getNumber())) {
            return new ResponseEntity<>("The account of destination does not exist.", HttpStatus.FORBIDDEN);
        }

        // Verificar que la cuenta de destino pertenezca al cliente autenticado
        if (!accountDestination.getClient().equals(client)) {
            return new ResponseEntity<>("The account of destination must be the same as the client who is logged in.", HttpStatus.FORBIDDEN);
        }


        // Se debe crear una solicitud de préstamo con el monto solicitado sumando un % para luego pagarlo
        // Mortage = 20%    ;   Personal = 30%  ;   Automotive = 40%
        if (loan.getName().equalsIgnoreCase("Mortgage")) {
            Double amount20 = (loanApplication.getAmount() * 1.20);
            ClientLoan clientLoan = new ClientLoan(amount20, loanApplication.getPayments());

            client.addClientLoan(clientLoan);
            loan.addClientLoan(clientLoan);

            clientLoanRepository.save(clientLoan);
        }
        if (loan.getName().equalsIgnoreCase("Personal")) {
            Double amount30 = (loanApplication.getAmount() * 1.30);
            ClientLoan clientLoan = new ClientLoan(amount30, loanApplication.getPayments());

            client.addClientLoan(clientLoan);
            loan.addClientLoan(clientLoan);

            clientLoanRepository.save(clientLoan);
        }
        if (loan.getName().equalsIgnoreCase("Automotive")) {
            Double amount40 = (loanApplication.getAmount() * 1.40);
            ClientLoan clientLoan = new ClientLoan(amount40, loanApplication.getPayments());

            client.addClientLoan(clientLoan);
            loan.addClientLoan(clientLoan);

            clientLoanRepository.save(clientLoan);
        }

        // Crear las transacciones tipo CREDIT, guardarlas en el repositorio y setear el nuevo balance y guardarlo
        LocalDateTime date = LocalDateTime.now();
        Transaction transactionLoan = new Transaction(TransactionType.CREDIT, loanApplication.getAmount(), date, loan.getName() + " Loan approved");
        accountDestination.addTransaction(transactionLoan);

        transactionRepository.save(transactionLoan);
        accountDestination.setBalance(accountDestination.getBalance() + loanApplication.getAmount());
        accountRepository.save(accountDestination);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
