package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "name")
    @GenericGenerator(name = "name", strategy = "native")
    private Long id;

    // cardHolder ??
    private CardType type;
    private CardColor color;
    private String number;
    private short cvv;
    private LocalDate thruDate;
    private LocalDate fromDate;
}