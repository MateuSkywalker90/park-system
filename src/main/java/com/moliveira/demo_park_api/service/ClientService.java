package com.moliveira.demo_park_api.service;

import com.moliveira.demo_park_api.entity.Client;
import com.moliveira.demo_park_api.exception.CpfUniqueViolationException;
import com.moliveira.demo_park_api.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public Client save(Client client) {
        try {
            return clientRepository.save(client);
        } catch (DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(
                    String.format("CPF '%s' already registered in the system", client.getCpf()));
        }
    }
}
