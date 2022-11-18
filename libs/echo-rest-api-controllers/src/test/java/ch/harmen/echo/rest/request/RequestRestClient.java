package ch.harmen.echo.rest.request;

import ch.harmen.echo.request.RequestConstants;
import ch.harmen.echo.rest.request.CreateRequestResultDto;
import ch.harmen.echo.rest.request.RequestDto;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

public class RequestRestClient {

  private static final ParameterizedTypeReference<List<RequestDto>> REQUEST_DTO_LIST = new ParameterizedTypeReference<>() {};
  private final WebTestClient webTestClient;

  public RequestRestClient(final WebTestClient webTestClient) {
    this.webTestClient = Objects.requireNonNull(webTestClient);
  }

  public CreateRequestResultDto create(
    final String endpointId,
    final String requestBody
  ) {
    return create(endpointId, HttpHeaders.EMPTY, requestBody);
  }

  public CreateRequestResultDto create(
    final String endpointId,
    final HttpHeaders headers,
    final String requestBody
  ) {
    return this.webTestClient.post()
      .uri(
        RequestConstants.REQUESTS_PATH,
        Map.of(RequestConstants.ENDPOINT_ID_PATH_VARIABLE, endpointId)
      )
      .headers(it -> it.addAll(headers))
      .bodyValue(requestBody)
      .exchange()
      .expectStatus()
      .isCreated()
      .expectBody(CreateRequestResultDto.class)
      .returnResult()
      .getResponseBody();
  }

  public EntityExchangeResult<RequestDto> get(
    final String endpointId,
    final String id
  ) {
    return get(RequestDto.class, endpointId, id);
  }

  public <T> EntityExchangeResult<T> get(
    final Class<T> expectedResponseBodyType,
    final String endpointId,
    final String id
  ) {
    return this.webTestClient.get()
      .uri(
        RequestConstants.REQUEST_PATH,
        Map.of(
          RequestConstants.ENDPOINT_ID_PATH_VARIABLE,
          endpointId,
          RequestConstants.ID_PATH_VARIABLE,
          id
        )
      )
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }

  public EntityExchangeResult<Void> delete(final String endpointId, String id) {
    return delete(Void.class, endpointId, id);
  }

  public <T> EntityExchangeResult<T> delete(
    final Class<T> expectedResponseBodyType,
    final String endpointId,
    String id
  ) {
    return this.webTestClient.delete()
      .uri(
        RequestConstants.REQUEST_PATH,
        Map.of(
          RequestConstants.ENDPOINT_ID_PATH_VARIABLE,
          endpointId,
          RequestConstants.ID_PATH_VARIABLE,
          id
        )
      )
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }

  public EntityExchangeResult<List<RequestDto>> get(
    final String endpointId,
    final Optional<Integer> page,
    final Optional<Integer> pageSize
  ) {
    return this.get(REQUEST_DTO_LIST, endpointId, page, pageSize);
  }

  public <T> EntityExchangeResult<T> get(
    final Class<T> expectedResponseBodyType,
    final String endpointId,
    final Optional<Integer> page,
    final Optional<Integer> pageSize
  ) {
    return this.webTestClient.get()
      .uri(
        RequestConstants.REQUESTS_PATH + "?page={page}&pageSize={pageSize}",
        endpointId,
        page.orElse(null),
        pageSize.orElse(null)
      )
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }

  public <T> EntityExchangeResult<T> get(
    final ParameterizedTypeReference<T> expectedResponseBodyType,
    final String endpointId,
    final Optional<Integer> page,
    final Optional<Integer> pageSize
  ) {
    return this.webTestClient.get()
      .uri(
        RequestConstants.REQUESTS_PATH + "?page={page}&pageSize={pageSize}",
        endpointId,
        page.orElse(null),
        pageSize.orElse(null)
      )
      .exchange()
      .expectBody(expectedResponseBodyType)
      .returnResult();
  }
}
