package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;

import java.util.List;

public interface AccountService {

    List<AccountDTO> getAccountsDTO();

    void saveAccount(Account account);

    boolean existsByNumber(String number);

    Account findById(long id);

    AccountDTO getAccountDTO(long id);

    Account findByNumber(String number);
}