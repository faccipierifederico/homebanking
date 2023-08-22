package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

    @Autowired
    private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository,
                                      LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository){
		return (args -> {

            Client client1 = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("melbamorel123"));
            Client client2 = new Client("Ant", "Lager", "antlager@gmail.com", passwordEncoder.encode("antlager123"));


            // Another way to do it:

/*          Client client2 = new Client();
            client2.setFirstName("Ant");
            client2.setLastName("Lager");
            client2.setEmail("antlager@gmail.com");
            clientRepository.save(client2);             */

            // saving the clients on the DB

            clientRepository.save(client1);
            clientRepository.save(client2);

            // Melba's acccounts

            Account account1 = new Account("VIN001", LocalDate.now(), 5000);
            Account account2 = new Account("VIN002", LocalDate.now().plusDays(1), 7500);

            // The others account

            Account account3 = new Account("VIN003", LocalDate.now(), 1000);
            Account account4 = new Account("VIN004", LocalDate.now(), 3500);

            // Saving the accounts with their respective clients.

            client1.addAccount(account1);
            client1.addAccount(account2);
            client2.addAccount(account3);
            client2.addAccount(account4);

            // Saving the accounts into the DB

            accountRepository.save(account1);
            accountRepository.save(account2);
            accountRepository.save(account3);
            accountRepository.save(account4);

            clientRepository.save(client1);
            clientRepository.save(client2);

            // Creating the transactions

            Transaction transaction1 = new Transaction(TransactionType.DEBIT, -350.0, LocalDateTime.now(), "Rent");
            Transaction transaction2 = new Transaction(TransactionType.CREDIT, 1150.0, LocalDateTime.now(), "Fee");
            Transaction transaction3 = new Transaction(TransactionType.DEBIT, -115.0, LocalDateTime.now(), "Scholarship");

            // Saving the transactions with their respective accounts.

            account1.addTransaction(transaction1);
            account1.addTransaction(transaction3);
            account2.addTransaction(transaction2);


            transactionRepository.save(transaction1);
            transactionRepository.save(transaction2);
            transactionRepository.save(transaction3);

            accountRepository.save(account1);
            accountRepository.save(account2);

            // Creating the transactions

            Loan loan1 = new Loan("Mortgage", 500000, List.of(12, 24, 36, 48, 60));
            Loan loan2 = new Loan("Personal", 100000, List.of(6, 12, 24));
            Loan loan3 = new Loan("Automotive", 300000, List.of(6, 12, 24, 36));

            // Saving the loans into the DB

            loanRepository.save(loan1);
            loanRepository.save(loan2);
            loanRepository.save(loan3);

            // Creating the ClientLoans

            ClientLoan clientLoan1 = new ClientLoan(400000, 60);
            ClientLoan clientLoan2 = new ClientLoan(50000, 12);
            ClientLoan clientLoan3 = new ClientLoan(100000, 24);
            ClientLoan clientLoan4 = new ClientLoan(200000, 36);

            client1.addClientLoan(clientLoan1);
            client1.addClientLoan(clientLoan2);
            client2.addClientLoan(clientLoan3);
            client2.addClientLoan(clientLoan4);
            loan1.addClientLoan(clientLoan1);
            loan2.addClientLoan(clientLoan2);
            loan2.addClientLoan(clientLoan3);
            loan3.addClientLoan(clientLoan4);

            clientRepository.save(client1);
            clientRepository.save(client2);
            loanRepository.save(loan1);
            loanRepository.save(loan2);
            loanRepository.save(loan3);
            clientLoanRepository.save(clientLoan1);
            clientLoanRepository.save(clientLoan2);
            clientLoanRepository.save(clientLoan3);
            clientLoanRepository.save(clientLoan4);

            Card card1 = new Card("Melba Morel", CardType.DEBIT, CardColor.GOLD, "1987 2579 6122 9911", (short) 954, LocalDate.now(),
                    LocalDate.now().plusYears(5));

            Card card2 = new Card("Melba Morel", CardType.CREDIT, CardColor.TITANIUM, "1987 2579 6599 7744", (short) 167, LocalDate.now(),
                    LocalDate.now().plusYears(5));

            Card card3 = new Card("Ant Lager", CardType.CREDIT, CardColor.SILVER, "1987 2579 9752 5151", (short) 456, LocalDate.now(),
                    LocalDate.now().plusYears(5));

            cardRepository.save(card1);
            cardRepository.save(card2);
            cardRepository.save(card3);

            client1.addCard(card1);
            client1.addCard(card2);
            client2.addCard(card3);

            clientRepository.save(client1);
            clientRepository.save(client2);
            cardRepository.save(card1);
            cardRepository.save(card2);
            cardRepository.save(card3);

        });
	}

}
