package ch.harmen.echo.rest.endpoint;

import ch.harmen.echo.endpoint.EndpointConstants;
import ch.harmen.echo.rest.endpoint.EndpointDto;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

public class EndpointRestClient {

  private static final ParameterizedTypeReference<List<EndpointDto>> ENDPOINT_DTO_LIST = new ParameterizedTypeReference<>() {};
  private final WebTestClient webTestClient;

  public EndpointRestClient(final WebTestClient webTestClient) {
    this.webTestClient = Objects.requireNonNull(webTestClient);
  }

  public void deleteAllEndpoints() {
    Optional
      .ofNullable(
        get(
          Optional.of(0),
          Optional.of(EndpointConstants.MAX_ENDPOINTS_PER_OWNER)
        )
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList)
      .stream()
      .map(EndpointDto::id)
      .forEach(this::delete);
  }

  public EndpointDto create() {
    return this.webTestClient.post()
      .uri(EndpointConstants.ENDPOINTS_PATH)
      .exchange()
      .expectStatus()
      .isCreated()
      .expectBody(EndpointDto.class)
      .returnResult()
      .getResponseBody();
  }

  public <T> EntityExchangeResult<T> create(
    final Class<T> expectedResponseBodyType
  ) {
    return this.webTestClient.post()
      .uri(EndpointConstants.ENDPOINTS_PATH)
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }

  public <T> EntityExchangeResult<T> create(
    final ParameterizedTypeReference<T> expectedResponseBodyType
  ) {
    return this.webTestClient.post()
      .uri(EndpointConstants.ENDPOINTS_PATH)
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }

  public EntityExchangeResult<EndpointDto> get(final String id) {
    return get(EndpointDto.class, id);
  }

  public <T> EntityExchangeResult<T> get(
    final Class<T> expectedResponseBodyType,
    final String id
  ) {
    return this.webTestClient.get()
      .uri(EndpointConstants.ENDPOINTS_PATH + "/{id}", id)
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }

  public EntityExchangeResult<Void> delete(final String id) {
    return delete(Void.class, id);
  }

  public <T> EntityExchangeResult<T> delete(
    final Class<T> expectedResponseBodyType,
    final String id
  ) {
    return this.webTestClient.delete()
      .uri(EndpointConstants.ENDPOINTS_PATH + "/{id}", id)
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }

  public EntityExchangeResult<List<EndpointDto>> get(
    final Optional<Integer> page,
    final Optional<Integer> pageSize
  ) {
    return this.get(ENDPOINT_DTO_LIST, page, pageSize);
  }

  public <T> EntityExchangeResult<T> get(
    final Class<T> expectedResponseBodyType,
    final Optional<Integer> page,
    final Optional<Integer> pageSize
  ) {
    return this.webTestClient.get()
      .uri(
        EndpointConstants.ENDPOINTS_PATH + "?page={page}&pageSize={pageSize}",
        page.orElse(null),
        pageSize.orElse(null)
      )
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }

  public <T> EntityExchangeResult<T> get(
    final ParameterizedTypeReference<T> expectedResponseBodyType,
    final Optional<Integer> page,
    final Optional<Integer> pageSize
  ) {
    return this.webTestClient.get()
      .uri(
        EndpointConstants.ENDPOINTS_PATH + "?page={page}&pageSize={pageSize}",
        page.orElse(null),
        pageSize.orElse(null)
      )
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }
}
