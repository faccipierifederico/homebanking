package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository){
		return (args -> {

            Client client1 = new Client("Melba", "Morel", "melba@mindhub.com");
            Client client2 = new Client("Ant", "Lager", "antlager@gmail.com");


            // Another way to do it:

/*          Client client2 = new Client();
            client2.setFirstName("Ant");
            client2.setLastName("Lager");
            client2.setEmail("antlager@gmail.com");
            clientRepository.save(client2);             */

            // Melba's acccounts

            Account account1 = new Account("VIN001", LocalDate.now(), 5000);
			LocalDate today =  LocalDate.now();
            Account account2 = new Account("VIN002", today.plusDays(1), 7500);

            // The others accounts

            Account account3 = new Account("VIN003", LocalDate.now(), 1000);
            Account account4 = new Account("VIN004", LocalDate.now(), 3500);

            // asignación de cuentas a los clientes

            clientRepository.save(client1);
            clientRepository.save(client2);

            client1.addAccount(account1);
            client1.addAccount(account2);
            client2.addAccount(account3);
            client2.addAccount(account4);

            accountRepository.save(account1);
            accountRepository.save(account2);
            accountRepository.save(account3);
            accountRepository.save(account4);

            clientRepository.save(client1);
            clientRepository.save(client2);
        });
	}

}
