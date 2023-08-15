package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;

public class ClientLoanDTO {

    private Long id;
    private String name;
    private Long loanId;
    private long amount;
    private Integer payments;

    public ClientLoanDTO() {
    }

    public ClientLoanDTO(ClientLoan clientLoan) {
        this.id = clientLoan.getId();
        this.name = clientLoan.getLoan().getName();
        this.loanId = clientLoan.getLoan().getId();
        this.amount = clientLoan.getAmount();
        this.payments = clientLoan.getPayments();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getLoanId() {
        return loanId;
    }

    public long getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }
}
