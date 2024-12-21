package com.moliveira.demo_park_api.web.dto.mapper;

import com.moliveira.demo_park_api.entity.CustomerVacancy;
import com.moliveira.demo_park_api.web.dto.ParkingCreateDto;
import com.moliveira.demo_park_api.web.dto.ParkingResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerVacancyMapper {

    public static CustomerVacancy toCustomerVacancy(ParkingCreateDto dto) {
        return new ModelMapper().map(dto, CustomerVacancy.class);
    }

    public static ParkingResponseDto toDto(CustomerVacancy customerVacancy) {
        return new ModelMapper().map(customerVacancy, ParkingResponseDto.class);
    }
}
