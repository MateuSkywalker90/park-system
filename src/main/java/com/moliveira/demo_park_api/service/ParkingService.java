package com.moliveira.demo_park_api.service;

import com.moliveira.demo_park_api.entity.Client;
import com.moliveira.demo_park_api.entity.CustomerVacancy;
import com.moliveira.demo_park_api.entity.ParkingSpace;
import com.moliveira.demo_park_api.util.ParkingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final CustomerVacancyService customerVacancyService;
    private final ClientService clientService;
    private final ParkingSpaceService parkingSpaceService;

    @Transactional
    public CustomerVacancy checkIn(CustomerVacancy customerVacancy) {
        Client client = clientService.findByCpf(customerVacancy.getClient().getCpf());
        customerVacancy.setClient(client);

        ParkingSpace parkingSpace = parkingSpaceService.findFreeParkingSpace();
        parkingSpace.setStatus(ParkingSpace.ParkingSpaceStatus.OCCUPIED);
        customerVacancy.setSpace(parkingSpace);

        customerVacancy.setEntryDate(LocalDateTime.now());

        customerVacancy.setReceipt(ParkingUtils.createReceipt());

        return customerVacancyService.save(customerVacancy);
    }

    @Transactional
    public CustomerVacancy checkOut(String receipt) {
        //Implement method
        return null;
    }
}
