package com.moliveira.demo_park_api.web.controller;

import com.moliveira.demo_park_api.entity.CustomerVacancy;
import com.moliveira.demo_park_api.service.ParkingService;
import com.moliveira.demo_park_api.web.dto.ParkingCreateDto;
import com.moliveira.demo_park_api.web.dto.ParkingResponseDto;
import com.moliveira.demo_park_api.web.dto.mapper.CustomerVacancyMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking-lots")
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDto> checkIn(@RequestBody @Valid ParkingCreateDto dto) {
        CustomerVacancy customerVacancy = CustomerVacancyMapper.toCustomerVacancy(dto);
        parkingService.checkIn(customerVacancy);
        ParkingResponseDto responseDto = CustomerVacancyMapper.toDto(customerVacancy);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{receipt}")
                .buildAndExpand(customerVacancy.getReceipt())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
    }
}
