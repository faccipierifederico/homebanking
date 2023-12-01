package com.mindhub.homebanking;

        import com.mindhub.homebanking.models.*;
        import com.mindhub.homebanking.repositories.*;
        import org.hamcrest.Matcher;
        import org.junit.jupiter.api.Test;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
        import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
        import static org.hamcrest.MatcherAssert.assertThat;
        import static org.hamcrest.Matchers.*;

        import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void existAccounts() {
        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts, is(not(empty())));
    }

    @Test
    public void numberNotNull() {
        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts, hasItem(hasProperty("number", notNullValue())));
    }
}