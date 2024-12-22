package com.moliveira.demo_park_api.service;

import com.moliveira.demo_park_api.entity.CustomerVacancy;
import com.moliveira.demo_park_api.exception.EntityNotFoundException;
import com.moliveira.demo_park_api.repository.CustomerVacancyRepository;
import com.moliveira.demo_park_api.repository.projection.CustomerVacancyProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public CustomerVacancy findByReceipt(String receipt) {
        return customerVacancyRepository.findByReceiptAndDepartureDateIsNull(receipt).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Receipt '%s' not found or vehicle has already left", receipt)
                )
        );
    }

    @Transactional(readOnly = true)
    public long getTotalTimesParked(String cpf) {
        return customerVacancyRepository.countByClientCpfAndEntryDateIsNotNull(cpf);
    }

    @Transactional(readOnly = true)
    public Page<CustomerVacancyProjection> findByClientCpf(String cpf, Pageable pageable) {
        return customerVacancyRepository.findAllByClientCpf(cpf, pageable);
    }

    @Transactional(readOnly = true)
    public Page<CustomerVacancyProjection> findAllByClientId(Long id, Pageable pageable) {
        return customerVacancyRepository.findAllByClientUserId(id, pageable);
    }
}
