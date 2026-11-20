package com.moliveira.demo_park_api.web.controller;

import com.moliveira.demo_park_api.entity.CustomerVacancy;
import com.moliveira.demo_park_api.jwt.JwtUserDetails;
import com.moliveira.demo_park_api.repository.projection.CustomerVacancyProjection;
import com.moliveira.demo_park_api.service.ClientService;
import com.moliveira.demo_park_api.service.CustomerVacancyService;
import com.moliveira.demo_park_api.service.JasperService;
import com.moliveira.demo_park_api.service.ParkingService;
import com.moliveira.demo_park_api.web.dto.PageableDto;
import com.moliveira.demo_park_api.web.dto.ParkingCreateDto;
import com.moliveira.demo_park_api.web.dto.ParkingResponseDto;
import com.moliveira.demo_park_api.web.dto.mapper.CustomerVacancyMapper;
import com.moliveira.demo_park_api.web.dto.mapper.PageableMapper;
import com.moliveira.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Parking", description = "Contains all parking resource operations such as creating, reading, and editing.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking-lots")
public class ParkingController {

    private final ParkingService parkingService;
    private final CustomerVacancyService customerVacancyService;
    private final ClientService clientService;
    private final JasperService jasperService;

    @Operation(summary = "Check-in Operation", description = "Resource to register the entry date of a vehicle in the parking. " +
            "Requisition needs a bearer token. Access allowed to 'ADMIN' role",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resource successfully created",
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

    @Operation(summary = "Return a receipt information", description = "feature to search for an existing receipt in the system. " +
            "Requisition needs a bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "receipt", description = "Number of receipt created by check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource located successfully",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Receipt number not found.",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
            })
    @GetMapping("/check-in/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ParkingResponseDto> getByReceipt(@PathVariable String receipt) {
        CustomerVacancy customerVacancy = customerVacancyService.findByReceipt(receipt);
        ParkingResponseDto dto = CustomerVacancyMapper.toDto(customerVacancy);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Check-out Operation", description = "Resource to register the departure date of a vehicle in the parking. " +
            "Requisition needs a bearer token. Access allowed to 'ADMIN' role",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "receipt", description = "Number of receipt created by check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource successfully updated",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Invalid number of receipt or " +
                            "the vehicle has already left the parking lot.",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to CLIENT role",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
            })
    @PutMapping("/check-out/{receipt}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDto> checkOut(@PathVariable String receipt) {
        CustomerVacancy customerVacancy = parkingService.checkOut(receipt);
        ParkingResponseDto dto = CustomerVacancyMapper.toDto(customerVacancy);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Find customer parking records by CPF", description = "Resource to find customer parking records by CPF. " +
            "Requisition needs a bearer token. Access allowed to 'ADMIN' role",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "cpf", description = "CPF number for the client to be consulted",
                            required = true
                    ),
                    @Parameter(in = QUERY, name = "page", description = "Represents the returned page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = QUERY, name = "size", description = "Represents total of elements in the page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = QUERY, name = "sort", description = "Represents the content order by 'entryDate, asc'.",
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "entryDate,asc")),
                            hidden = true
                    ),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource successfully located",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to CLIENT role",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
            })
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getAllParkingLotsByCpf(@PathVariable String cpf, @Parameter(hidden = true)
    @PageableDefault(size = 5, sort = "entryDate", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CustomerVacancyProjection> projection = customerVacancyService.findByClientCpf(cpf, pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Find customer parking records by user id", description = "Resource to find customer parking records by his user id. " +
            "Requisition needs a bearer token. Access allowed to 'CLIENT' role",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = QUERY, name = "page", description = "Represents the returned page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = QUERY, name = "size", description = "Represents total of elements in the page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = QUERY, name = "sort", description = "Represents the content order by 'entryDate, asc'.",
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "entryDate,asc")),
                            hidden = true
                    ),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource successfully located",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to ADMIN role",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
            })
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PageableDto> getAllClientParkingLots(@AuthenticationPrincipal JwtUserDetails user,
                                                               @PageableDefault(size = 5, sort = "entryDate",
                                                               direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CustomerVacancyProjection> projection = customerVacancyService.findAllByClientId(user.getId(), pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> getReport(HttpServletResponse response, @AuthenticationPrincipal JwtUserDetails userDetails) throws IOException {
        String cpf = clientService.findUserById(userDetails.getId()).getCpf();
        byte[] bytes = jasperService.generateParkingReport(cpf);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-disposition", "inline; filename=" + System.currentTimeMillis() + ".pdf");
        response.getOutputStream().write(bytes);

        return ResponseEntity.ok().build();
    }
}
