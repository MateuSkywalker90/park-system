package com.moliveira.demo_park_api.repository;

import com.moliveira.demo_park_api.entity.CustomerVacancy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerVacancyRepository extends JpaRepository<CustomerVacancy, Long> {
}
