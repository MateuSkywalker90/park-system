package com.moliveira.demo_park_api.web.controller;

import com.moliveira.demo_park_api.entity.CustomerVacancy;
import com.moliveira.demo_park_api.service.CustomerVacancyService;
import com.moliveira.demo_park_api.service.ParkingService;
import com.moliveira.demo_park_api.web.dto.ParkingCreateDto;
import com.moliveira.demo_park_api.web.dto.ParkingResponseDto;
import com.moliveira.demo_park_api.web.dto.mapper.CustomerVacancyMapper;
import com.moliveira.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Parking", description = "Contains all parking resource operations such as creating, reading, and editing.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking-lots")
public class ParkingController {

    private final ParkingService parkingService;
    private final CustomerVacancyService customerVacancyService;

    @Operation(summary = "Check-in Operation", description = "Resource to register the entry date of a vehicle in the parking. " +
            "Requisition needs a bearer token. Access allowed to 'ADMIN' role",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resource created successfully",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL to access the created resource"),
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Possible causes: <br/>" +
                            "-Customer CPF not registered in the system; <br/>" +
                            "-All parking spaces are occupied;",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed due to lost or invalid data",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to CLIENT role",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
            })
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

    @GetMapping("/check-in/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ParkingResponseDto> getByReceipt(@PathVariable String receipt) {
        CustomerVacancy customerVacancy = customerVacancyService.findByReceipt(receipt);
        ParkingResponseDto dto = CustomerVacancyMapper.toDto(customerVacancy);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/check-out/{receipt}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDto> checkOut(@PathVariable String receipt) {
        CustomerVacancy customerVacancy = parkingService.checkOut(receipt);
        ParkingResponseDto dto = CustomerVacancyMapper.toDto(customerVacancy);
        return ResponseEntity.ok(dto);
    }
}
