package com.moliveira.demo_park_api.repository;

import com.moliveira.demo_park_api.entity.CustomerVacancy;
import com.moliveira.demo_park_api.repository.projection.CustomerVacancyProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerVacancyRepository extends JpaRepository<CustomerVacancy, Long> {

    Optional<CustomerVacancy> findByReceiptAndDepartureDateIsNull(String receipt);

    long countByClientCpfAndEntryDateIsNotNull(String cpf);

    Page<CustomerVacancyProjection> findAllByClientCpf(String cpf, Pageable pageable);

    Page<CustomerVacancyProjection> findAllByClientUserId(Long id, Pageable pageable);
}
