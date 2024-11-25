package com.moliveira.demo_park_api.web.controller;

import com.moliveira.demo_park_api.entity.User;
import com.moliveira.demo_park_api.service.UserService;
import com.moliveira.demo_park_api.web.dto.UserCreateDto;
import com.moliveira.demo_park_api.web.dto.UserPasswordDto;
import com.moliveira.demo_park_api.web.dto.UserResponseDto;
import com.moliveira.demo_park_api.web.dto.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Contains all user resource operations such as creating, reading, and editing a user.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Resource to create a new user",
            responses = {
                @ApiResponse(responseCode = "201", description = "User successfully created",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                @ApiResponse(responseCode = "409", description = "User e-mail already exists",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(responseCode = "422", description = "Resource not processed, data is invalid",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                )
            })
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto createDto) {
        User responseUser = userService.save(UserMapper.toUser(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(responseUser));
    }

    @Operation(summary = "Find a user by his id", description = "Find a user by his id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        User responseUser = userService.searchById(id);
        return ResponseEntity.ok(UserMapper.toDto(responseUser));
    }

    @Operation(summary = "Update user password", description = "Update user password",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Password successfully updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "Wrong password",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordDto dto) {
        User responseUser = userService.changePassword(id, dto.getActualPassword(), dto.getNewPassword(), dto.getConfirmedPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all users", description = "Return all users in the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All active users successfully found",
                            content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class))))
            })
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<User> responseUser = userService.findAll();
        return ResponseEntity.ok(UserMapper.toListDto(responseUser));
    }
}
