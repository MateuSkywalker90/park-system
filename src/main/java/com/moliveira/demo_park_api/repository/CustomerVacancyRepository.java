package com.moliveira.demo_park_api.repository;

import com.moliveira.demo_park_api.entity.CustomerVacancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerVacancyRepository extends JpaRepository<CustomerVacancy, Long> {

    Optional<CustomerVacancy> findByReceiptAndDepartureDateIsNull(String receipt);
}
