package com.moliveira.demo_park_api.web.controller;

import com.moliveira.demo_park_api.entity.Client;
import com.moliveira.demo_park_api.jwt.JwtUserDetails;
import com.moliveira.demo_park_api.repository.projection.ClientProjection;
import com.moliveira.demo_park_api.service.ClientService;
import com.moliveira.demo_park_api.service.UserService;
import com.moliveira.demo_park_api.web.dto.ClientCreateDto;
import com.moliveira.demo_park_api.web.dto.ClientResponseDto;
import com.moliveira.demo_park_api.web.dto.PageableDto;
import com.moliveira.demo_park_api.web.dto.UserResponseDto;
import com.moliveira.demo_park_api.web.dto.mapper.ClientMapper;
import com.moliveira.demo_park_api.web.dto.mapper.PageableMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clients")
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;

    @Operation(summary = "Create a new client", description = "Resource to create and bind a new client to a system user. " +
            "Access required Role='CLIENT'",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Client successfully created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Client cpf already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed, data is invalid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to ADMIN role",
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

    @Operation(summary = "Find a client by id", description = "Resource to find a client registered in the system. " +
            "Access required Role='ADMIN'",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Client successfully found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Client not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed to CLIENT role",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientResponseDto> getById(@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok(ClientMapper.toDto(client));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getAll(Pageable pageable) {
        Page<ClientProjection> clients = clientService.findAll(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(clients));
    }
}
