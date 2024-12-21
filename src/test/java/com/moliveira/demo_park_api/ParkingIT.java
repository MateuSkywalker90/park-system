package com.moliveira.demo_park_api;

import com.moliveira.demo_park_api.web.dto.ParkingCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/parking/parking-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/parking/parking-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ParkingIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createCheckIn_WithValidData_ReturnCreatedAndLocation() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("WER-1234").brand("FIAT").model("UNO 2021")
                .color("RED").clientCpf("88599201085")
                .build();

        testClient.post().uri("/api/v1/parking-lots/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("licensePlate").isEqualTo("WER-1234")
                .jsonPath("brand").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("UNO 2021")
                .jsonPath("color").isEqualTo("RED")
                .jsonPath("clientCpf").isEqualTo("88599201085")
                .jsonPath("receipt").exists()
                .jsonPath("entryDate").exists()
                .jsonPath("parkingSpaceCode").exists();
    }

    @Test
    public void createCheckIn_WithRoleClient_ReturnErrorStatus403() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("WER-1234").brand("FIAT").model("UNO 2021")
                .color("RED").clientCpf("88599201085")
                .build();

        testClient.post().uri("/api/v1/parking-lots/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckIn_WithInvalidData_ReturnErrorStatus422() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("").brand("").model("")
                .color("").clientCpf("")
                .build();

        testClient.post().uri("/api/v1/parking-lots/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo("422")
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckIn_WithNonExistentCpf_ReturnErrorStatus404() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("WER-1234").brand("FIAT").model("UNO 2021")
                .color("RED").clientCpf("59177724011")
                .build();

        testClient.post().uri("/api/v1/parking-lots/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Sql(scripts = "/sql/parking/parking-space-occupied-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/parking/parking-space-occupied-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void createCheckIn_WithOccupiedParkingSpaces_ReturnErrorStatus404() {
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .licensePlate("WER-1234").brand("FIAT").model("UNO 2021")
                .color("RED").clientCpf("88599201085")
                .build();

        testClient.post().uri("/api/v1/parking-lots/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-in")
                .jsonPath("method").isEqualTo("POST");
    }
}
