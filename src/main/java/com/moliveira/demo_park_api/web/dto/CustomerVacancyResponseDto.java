package com.moliveira.demo_park_api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVacancyResponseDto {

    private String licensePlate;
    private String brand;
    private String model;
    private String color;
    private String clientCpf;
    private String receipt;
    private LocalDateTime entryDate;
    private LocalDateTime departureDate;
    private String parkingSpaceCode;
    private BigDecimal amount;
    private BigDecimal discount;
}
