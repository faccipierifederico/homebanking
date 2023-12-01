package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.models.CardType.CREDIT;
import static com.mindhub.homebanking.models.CardType.DEBIT;

@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private CardService cardService;

    @PostMapping(path = "clients/current/cards")
    public ResponseEntity<Object> register(Authentication authentication, @RequestParam CardType cardType, @RequestParam CardColor cardColor) {
        Client client = clientService.findByEmail(authentication.getName());

        if (cardType == null) {
            return new ResponseEntity<>("You must choose a card type.", HttpStatus.FORBIDDEN);
        }
        if (cardColor == null) {
            return new ResponseEntity<>("You must choose a card color.", HttpStatus.FORBIDDEN);
        }

        Set<Card> cardsType = client.getCards().stream().filter(card -> card.getType() == cardType).collect(Collectors.toSet());

        boolean noRepeatColorAndType = cardsType.stream().anyMatch(card -> card.getColor() == cardColor);

        if (noRepeatColorAndType) {
            return new ResponseEntity<>("You already have one " + cardColor + " " + cardType + " card.", HttpStatus.FORBIDDEN);
        }

        if (cardsType.size() >= 3) {
            return new ResponseEntity<>("You already have 3 or more of this type (" + cardType + ") of cards.", HttpStatus.FORBIDDEN);
        }

        String number = CardUtils.getCardNumber();

        Short cvv = CardUtils.getCVV();

        String cardHolder = client.getLastName() + " " + client.getFirstName();
        LocalDate thruDate = LocalDate.now();
        LocalDate fromDate = LocalDate.now().plusYears(5);

        Card card = new Card(cardHolder, cardType, cardColor, number, cvv, thruDate, fromDate);

        client.addCard(card);
        cardService.saveCard(card);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }

}
