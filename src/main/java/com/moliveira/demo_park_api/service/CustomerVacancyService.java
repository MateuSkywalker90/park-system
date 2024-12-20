package com.moliveira.demo_park_api.service;

import com.moliveira.demo_park_api.entity.CustomerVacancy;
import com.moliveira.demo_park_api.repository.CustomerVacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomerVacancyService {

    private final CustomerVacancyRepository customerVacancyRepository;

    @Transactional
    public CustomerVacancy save(CustomerVacancy customerVacancy) {
        return customerVacancyRepository.save(customerVacancy);
    }
}
