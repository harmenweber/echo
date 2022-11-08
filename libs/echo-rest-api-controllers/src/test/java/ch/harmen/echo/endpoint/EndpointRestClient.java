package ch.harmen.echo.endpoint;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

public class EndpointRestClient {

  private static final ParameterizedTypeReference<List<EndpointDto>> ENDPOINT_DTO_LIST = new ParameterizedTypeReference<>() {};
  private final WebTestClient webTestClient;

  public EndpointRestClient(final WebTestClient webTestClient) {
    this.webTestClient = Objects.requireNonNull(webTestClient);
  }

  EndpointDto createEndpoint() {
    return this.webTestClient.post()
      .uri(EndpointRestController.ENDPOINTS_PATH)
      .exchange()
      .expectStatus()
      .isCreated()
      .expectBody(EndpointDto.class)
      .returnResult()
      .getResponseBody();
  }

  <T> EntityExchangeResult<T> createEndpoint(
    final Class<T> expectedResponseBodyType
  ) {
    return this.webTestClient.post()
      .uri(EndpointRestController.ENDPOINTS_PATH)
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }

  <T> EntityExchangeResult<T> createEndpoint(
    final ParameterizedTypeReference<T> expectedResponseBodyType
  ) {
    return this.webTestClient.post()
      .uri(EndpointRestController.ENDPOINTS_PATH)
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }

  EntityExchangeResult<EndpointDto> getEndpoint(String id) {
    return this.webTestClient.get()
      .uri(EndpointRestController.ENDPOINTS_PATH + "/{id}", id)
      .exchange()
      .expectBody(EndpointDto.class)
      .returnResult();
  }

  FluxExchangeResult<Void> deleteEndpoint(String id) {
    return this.webTestClient.delete()
      .uri(EndpointRestController.ENDPOINTS_PATH + "/{id}", id)
      .exchange()
      .returnResult(Void.class);
  }

  EntityExchangeResult<List<EndpointDto>> getEndpoints(
    final Optional<Integer> page,
    final Optional<Integer> pageSize
  ) {
    return this.getEndpoints(ENDPOINT_DTO_LIST, page, pageSize);
  }

  <T> EntityExchangeResult<T> getEndpoints(
    final Class<T> expectedResponseBodyType,
    final Optional<Integer> page,
    final Optional<Integer> pageSize
  ) {
    return this.webTestClient.get()
      .uri(
        EndpointRestController.ENDPOINTS_PATH +
        "?page={page}&pageSize={pageSize}",
        page.orElse(null),
        pageSize.orElse(null)
      )
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }

  private <T> EntityExchangeResult<T> getEndpoints(
    final ParameterizedTypeReference<T> expectedResponseBodyType,
    final Optional<Integer> page,
    final Optional<Integer> pageSize
  ) {
    return this.webTestClient.get()
      .uri(
        EndpointRestController.ENDPOINTS_PATH +
        "?page={page}&pageSize={pageSize}",
        page.orElse(null),
        pageSize.orElse(null)
      )
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }
}
