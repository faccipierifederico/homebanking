package com.mindhub.homebanking;


import com.mindhub.homebanking.utils.CardUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class CardUtilsTests {

    @Test
    public void cardNumberIsCreated(){
        String cardNumber = CardUtils.getCardNumber();
        assertThat(cardNumber,is(not(emptyOrNullString())));
    }

    @Test
    public void cardNumberSize(){
        String cardNumber = CardUtils.getCardNumber();
        assertThat(cardNumber, containsString("-"));
    }

    @Test
    public void cvvIsCreated() {
        Short cvvNumber = CardUtils.getCVV();
        assertThat(cvvNumber, notNullValue());
    }

    @Test
    public void cvvIsLessThan() {
        Short cvvNumber = CardUtils.getCVV();
        assertThat(cvvNumber, isA(Short.class));
    }

}
