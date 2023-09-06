package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface ClientService {

    List<ClientDTO> getClientsDTO();

    void saveClient(Client client);

    Client findByEmail(String email);

    ClientDTO getClientDTO(String email);
}