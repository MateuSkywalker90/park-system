package com.moliveira.demo_park_api;

import com.moliveira.demo_park_api.web.dto.PageableDto;
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

    @Test
    public void findCheckIn_WithAdminRole_ReturnDataWithStatus200() {
        testClient.get()
                .uri("/api/v1/parking-lots/check-in/{receipt}", "20230313-101200")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("licensePlate").isEqualTo("CAD-4679")
                .jsonPath("brand").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("PALIO")
                .jsonPath("color").isEqualTo("GREEN")
                .jsonPath("clientCpf").isEqualTo("88617905000")
                .jsonPath("receipt").isEqualTo("20230313-101200")
                .jsonPath("entryDate").isEqualTo("2023-03-13 10:13:56")
                .jsonPath("parkingSpaceCode").isEqualTo("A-01");
    }

    @Test
    public void findCheckIn_WithClientRole_ReturnDataWithStatus200() {
        testClient.get().uri("/api/v1/parking-lots/check-in/{receipt}", "20230313-101200")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("licensePlate").isEqualTo("CAD-4679")
                .jsonPath("brand").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("PALIO")
                .jsonPath("color").isEqualTo("GREEN")
                .jsonPath("clientCpf").isEqualTo("88617905000")
                .jsonPath("receipt").isEqualTo("20230313-101200")
                .jsonPath("entryDate").isEqualTo("2023-03-13 10:13:56")
                .jsonPath("parkingSpaceCode").isEqualTo("A-01");
    }

    @Test
    public void findCheckIn_WithNonExistentReceipt_ReturnErrorStatus404() {
        testClient.get().uri("/api/v1/parking-lots/check-in/{receipt}", "20230313-999999")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-in/20230313-999999")
                .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void createCheckOut_WithExistentReceipt_ReturnSuccess() {
        testClient.put()
                .uri("/api/v1/parking-lots/check-out/{receipt}", "20241013-101200")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("licensePlate").isEqualTo("CAD-4679")
                .jsonPath("brand").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("PALIO")
                .jsonPath("color").isEqualTo("GREEN")
                .jsonPath("clientCpf").isEqualTo("88617905000")
                .jsonPath("receipt").isEqualTo("20241013-101200")
                .jsonPath("entryDate").isEqualTo("2024-10-13 10:13:56")
                .jsonPath("parkingSpaceCode").isEqualTo("A-01")
                .jsonPath("departureDate").exists()
                .jsonPath("amount").exists()
                .jsonPath("discount").exists();
    }

    @Test
    public void createCheckOut_WithNonExistentReceipt_ReturnErrorStatus404() {
        testClient.put()
                .uri("/api/v1/parking-lots/check-out/{receipt}", "20241013-000000")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-out/20241013-000000")
                .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void createCheckOut_WithClientRole_ReturnErrorStatus403() {
        testClient.put()
                .uri("/api/v1/parking-lots/check-out/{receipt}", "20241013-101200")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/check-out/20241013-101200")
                .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void findParkingLots_ByClientCpf_ReturnSuccess() {
        PageableDto responseBody = testClient.get()
                .uri("/api/v1/parking-lots/cpf/{cpf}?size=1&page=0", "88617905000")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = testClient.get()
                .uri("/api/v1/parking-lots/cpf/{cpf}?size=1&page=1", "88617905000")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);

    }

    @Test
    public void findParkingLots_ByClientCpfWithClientRole_ReturnErrorStatus403() {
        testClient.get()
                .uri("/api/v1/parking-lots/cpf/{cpf}", "88617905000")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/parking-lots/cpf/88617905000")
                .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void findParkingLots_ByClientUserId_ReturnSuccess() {
        PageableDto responseBody = testClient.get()
                .uri("/api/v1/parking-lots?size=1&page=0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = testClient.get()
                .uri("/api/v1/parking-lots?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);

    }

    @Test
    public void findParkingLots_ByClientUserIdWithAdminRole_ReturnErrorStatus403() {
        testClient.get()
                .uri("/api/v1/parking-lots")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/parking-lots")
                .jsonPath("method").isEqualTo("GET");
    }
}
