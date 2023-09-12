package com.mindhub.homebanking.utils;

public class CardUtils {

    public static Short getCVV() {
        Short cvv = (short) ((Math.random() * (1000 - 100)) + 100);
        return cvv;
    }

    public static String getCardNumber() {
        String number = (int) ((Math.random() * (10000 - 1000)) + 1000) + "-" + (int) ((Math.random() * (10000 - 1000)) + 1000)
                + "-" + (int) ((Math.random() * (10000 - 1000)) + 1000) + "-" + (int) ((Math.random() * (10000 - 1000)) + 1000);
        return number;
    }
}
