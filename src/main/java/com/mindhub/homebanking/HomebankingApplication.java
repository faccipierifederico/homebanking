package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository){
		return (args -> {

            Client client1 = new Client("Melba", "Morel", "melba@mindhub.com");
            Client client2 = new Client("Ant", "Lager", "antlager@gmail.com");


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
			LocalDate today =  LocalDate.now();
            Account account2 = new Account("VIN002", today.plusDays(1), 7500);

            // The others account

            Account account3 = new Account("VIN003", LocalDate.now(), 1000);
            Account account4 = new Account("VIN004", LocalDate.now(), 3500);

            // Saving the accounts with their respective clients.

            client1.addAccount(account1);
            client1.addAccount(account2);
            client2.addAccount(account3);
            client2.addAccount(account4);

            // saving the accounts into the DB

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


        });
	}

}
