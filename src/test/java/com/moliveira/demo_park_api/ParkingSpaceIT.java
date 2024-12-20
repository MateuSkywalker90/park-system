package com.moliveira.demo_park_api;

import com.moliveira.demo_park_api.web.dto.ParkingSpaceCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/parkingSpace/parking-space-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/parkingSpace/parking-space-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ParkingSpaceIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createParkingSpace_WithValidData_ReturnLocationStatus201() {
        testClient
                .post()
                .uri("/api/v1/parking-spaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new ParkingSpaceCreateDto("A-05", "FREE"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION);
    }

    @Test
    public void createParkingSpace_WithExistentCode_ReturnErrorMessageWithStatus409() {
        testClient
                .post()
                .uri("/api/v1/parking-spaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new ParkingSpaceCreateDto("A-01", "FREE"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("status").isEqualTo(409)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parking-spaces");
    }

    @Test
    public void createParkingSpace_WithInvalidData_ReturnErrorMessageWithStatus422() {
        testClient
                .post()
                .uri("/api/v1/parking-spaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new ParkingSpaceCreateDto("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parking-spaces");

        testClient
                .post()
                .uri("/api/v1/parking-spaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new ParkingSpaceCreateDto("A-501", "UNKNOWN"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parking-spaces");
    }

    @Test
    public void findParkingSpace_WithExistentCode_ReturnDataWithStatus200() {
        testClient
                .get()
                .uri("/api/v1/parking-spaces/{code}", "A-01")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(10)
                .jsonPath("code").isEqualTo("A-01")
                .jsonPath("status").isEqualTo("FREE");
    }

    @Test
    public void findParkingSpace_WithNonExistentCode_ReturnErrorMessageWithStatus404() {
        testClient
                .get()
                .uri("/api/v1/parking-spaces/{code}", "A-10")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("method").isEqualTo("GET")
                .jsonPath("path").isEqualTo("/api/v1/parking-spaces/A-10");
    }

    @Test
    public void createParkingSpace_WithUserWithoutPermission_ReturnErrorMessageWithStatus403() {
        testClient
                .post()
                .uri("/api/v1/parking-spaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .bodyValue(new ParkingSpaceCreateDto("A-05", "OCCUPIED"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parking-spaces");
    }

    @Test
    public void findParkingSpace_WithUserWithoutPermission_ReturnErrorMessageWithStatus403() {
        testClient
                .get()
                .uri("/api/v1/parking-spaces/{code}", "A-01")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("method").isEqualTo("GET")
                .jsonPath("path").isEqualTo("/api/v1/parking-spaces/A-01");
    }
}
