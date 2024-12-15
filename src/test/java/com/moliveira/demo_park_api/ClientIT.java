package com.moliveira.demo_park_api;

import com.moliveira.demo_park_api.web.dto.ClientCreateDto;
import com.moliveira.demo_park_api.web.dto.ClientResponseDto;
import com.moliveira.demo_park_api.web.dto.PageableDto;
import com.moliveira.demo_park_api.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clients/clients-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clients/clients-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ClientIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createClient_WithValidData_ReturnClientWithStatus201() {
        ClientResponseDto responseBody = testClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClientCreateDto("Tobby Maguire", "06036762003"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClientResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("Tobby Maguire");
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo("06036762003");
    }

    @Test
    public void createClient_WithRegisteredCpf_ReturnErrorMessageStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClientCreateDto("Tobby Maguire", "88617905000"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createClient_WithInvalidData_ReturnErrorMessageStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClientCreateDto("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClientCreateDto("Toby", "00000000000"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClientCreateDto("Toby", "060.367.620-03"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createClient_WithUserNotAllowed_ReturnErrorMessageStatus403() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new ClientCreateDto("Tobby Maguire", "88617905000"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findClient_WithExistentIdByAdmin_ReturnClientWithStatus200() {
        ClientResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/clients/10")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void findClient_WithNonExistentIdByAdmin_ReturnErrorMessageStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void findClient_WithExistentIdByClient_ReturnErrorMessageStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients/10")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findClient_WithPaginationByAdmin_ReturnClientWithStatus200() {
        PageableDto responseBody = testClient
                .get()
                .uri("/api/v1/clients")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1);

        responseBody = testClient
                .get()
                .uri("/api/v1/clients?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void findClient_WithPaginationByClient_ReturnErrorMessageStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findClient_WithDataOfClientToken_ReturnClientWithStatus200() {
        ClientResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/clients/details")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo("88599201085");
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("Bianca Silver");
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void findClient_WithDataOfAdminToken_ReturnErrorMessageStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients/details")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }
}
