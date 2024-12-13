package com.moliveira.demo_park_api.web.controller;

import com.moliveira.demo_park_api.entity.Client;
import com.moliveira.demo_park_api.jwt.JwtUserDetails;
import com.moliveira.demo_park_api.service.ClientService;
import com.moliveira.demo_park_api.service.UserService;
import com.moliveira.demo_park_api.web.dto.ClientCreateDto;
import com.moliveira.demo_park_api.web.dto.ClientResponseDto;
import com.moliveira.demo_park_api.web.dto.UserResponseDto;
import com.moliveira.demo_park_api.web.dto.mapper.ClientMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clients")
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;

    @Operation(summary = "Create a new client", description = "Resource to create and bind a new client to a system user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Client successfully created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Client cpf already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed, data is invalid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to ADMIN user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            })
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDto> create(@RequestBody @Valid ClientCreateDto dto,
                                                    @AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = ClientMapper.toClient(dto);
        client.setUser(userService.searchById(userDetails.getId()));
        clientService.save(client);
        return  ResponseEntity.status(201).body(ClientMapper.toDto(client));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getById(@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok(ClientMapper.toDto(client));
    }
}
