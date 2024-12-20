package com.moliveira.demo_park_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final CustomerVacancyService customerVacancyService;
    private final ClientService clientService;
    private final ParkingService parkingService;
}
