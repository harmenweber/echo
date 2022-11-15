package ch.harmen.echo.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import ch.harmen.echo.RestTestConfiguration;
import ch.harmen.echo.endpoint.EndpointDto;
import ch.harmen.echo.endpoint.EndpointRestClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.util.Base64Utils;

@SpringBootTest(classes = RestTestConfiguration.class)
public class RequestRestControllerTest {

  @Autowired
  private EndpointRestClient endpointRestClient;

  @Autowired
  private RequestRestClient requestRestClient;

  @BeforeEach
  void deleteAllExistingEndpoints() {
    this.endpointRestClient.deleteAllEndpoints();
  }

  @Test
  void createRequest() {
    final HttpHeaders httpHeaders = getRandomHttpHeaders();
    final String requestBody = UUID.randomUUID().toString();

    final EndpointDto endpoint = this.endpointRestClient.create();
    final String requestId =
      this.requestRestClient.create(endpoint.id(), httpHeaders, requestBody)
        .id();

    final RequestDto request =
      this.requestRestClient.get(endpoint.id(), requestId).getResponseBody();

    assertThat(request).isNotNull();
    assertAll(
      () -> assertThat(request.endpointId()).isEqualTo(endpoint.id()),
      () -> assertThat(request.id()).isEqualTo(requestId),
      () -> assertThat(request.method()).isEqualTo(HttpMethod.POST.name()),
      () -> assertThat(request.headers()).containsAllEntriesOf(httpHeaders),
      () ->
        assertThat(
          request
            .base64EncodedBody()
            .map(RequestRestControllerTest::decodeBase64ToString)
            .orElse(null)
        )
          .isEqualTo(requestBody)
    );
  }

  @Test
  void getRequest() {
    final EndpointDto endpoint = this.endpointRestClient.create();
    final String requestId =
      this.requestRestClient.create(endpoint.id(), UUID.randomUUID().toString())
        .id();

    final RequestDto request =
      this.requestRestClient.get(endpoint.id(), requestId).getResponseBody();

    assertThat(request).isNotNull();
  }

  @Test
  void getRequest_returnsNotFound_ifRequestDoesNotExist() {
    final EndpointDto endpoint = this.endpointRestClient.create();
    final String requestId = UUID.randomUUID().toString();

    final EntityExchangeResult<String[]> response =
      this.requestRestClient.get(String[].class, endpoint.id(), requestId);

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND),
      () ->
        assertThat(response.getResponseBody())
          .contains(
            "Request %s of endpoint %s not found.".formatted(
                requestId,
                endpoint.id()
              )
          )
    );
  }

  @Test
  void getRequest_returnsNotFound_ifEndpointDoesNotExist() {
    final String endpointId = UUID.randomUUID().toString();
    final String requestId = UUID.randomUUID().toString();

    final EntityExchangeResult<String[]> response =
      this.requestRestClient.get(String[].class, endpointId, requestId);

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND),
      () ->
        assertThat(response.getResponseBody())
          .contains("Endpoint %s not found.".formatted(endpointId))
    );
  }

  @Test
  void delete() {
    final EndpointDto endpoint = this.endpointRestClient.create();
    final String requestId =
      this.requestRestClient.create(endpoint.id(), UUID.randomUUID().toString())
        .id();

    final EntityExchangeResult<RequestDto> getRequestBeforeDeleteResponse =
      this.requestRestClient.get(endpoint.id(), requestId);

    final EntityExchangeResult<Void> deleteRequestResponse =
      this.requestRestClient.delete(endpoint.id(), requestId);

    final EntityExchangeResult<String[]> getRequestAfterDeleteResponse =
      this.requestRestClient.get(String[].class, endpoint.id(), requestId);

    assertAll(
      () ->
        assertThat(getRequestBeforeDeleteResponse.getStatus())
          .isEqualTo(HttpStatus.OK),
      () ->
        assertThat(deleteRequestResponse.getStatus()).isEqualTo(HttpStatus.OK),
      () ->
        assertThat(getRequestAfterDeleteResponse.getStatus())
          .isEqualTo(HttpStatus.NOT_FOUND)
    );
  }

  @Test
  void delete_returnsNotFound_ifRequestDoesNotExist() {
    final EndpointDto endpoint = this.endpointRestClient.create();
    final String requestId = UUID.randomUUID().toString();

    final EntityExchangeResult<String[]> response =
      this.requestRestClient.delete(String[].class, endpoint.id(), requestId);

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND),
      () ->
        assertThat(response.getResponseBody())
          .contains(
            "Request %s of endpoint %s not found.".formatted(
                requestId,
                endpoint.id()
              )
          )
    );
  }

  @Test
  void delete_returnsNotFound_ifEndpointDoesNotExist() {
    final String endpointId = UUID.randomUUID().toString();
    final String requestId = UUID.randomUUID().toString();

    final EntityExchangeResult<String[]> response =
      this.requestRestClient.delete(String[].class, endpointId, requestId);

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND),
      () ->
        assertThat(response.getResponseBody())
          .contains("Endpoint %s not found.".formatted(endpointId))
    );
  }

  @Test
  void getRequests_returnsRequestsOrderedByReceiveTimeDescending() {
    // Create an endpoint.
    final EndpointDto endpoint = this.endpointRestClient.create();

    // Create some requests.
    for (int i = 0; i < RequestConstants.MAX_REQUESTS_PER_ENDPOINT; i++) {
      this.requestRestClient.create(
          endpoint.id(),
          UUID.randomUUID().toString()
        );
    }

    // Fetch all requests.
    final EntityExchangeResult<List<RequestDto>> response =
      this.requestRestClient.get(
          endpoint.id(),
          Optional.empty(),
          Optional.of(RequestConstants.MAX_REQUESTS_PER_ENDPOINT)
        );

    final List<RequestDto> fetchedRequests = Optional
      .ofNullable(response.getResponseBody())
      .orElseGet(Collections::emptyList);

    final List<RequestDto> sortedRequests = new ArrayList<>(fetchedRequests);
    sortedRequests.sort(
      Comparator.comparing(RequestDto::receiveTime).reversed()
    );

    assertThat(fetchedRequests).isEqualTo(sortedRequests);
  }

  @Test
  void getRequests_respectsPageSize() {
    // Create an endpoint.
    final EndpointDto endpoint = this.endpointRestClient.create();

    // Create the maximum number of requests for that endpoint.
    for (int i = 0; i < RequestConstants.MAX_REQUESTS_PER_ENDPOINT; i++) {
      this.requestRestClient.create(
          endpoint.id(),
          UUID.randomUUID().toString()
        );
    }

    // Fetch requests with a specific page size.
    final int pageSize = RequestConstants.MAX_REQUESTS_PER_ENDPOINT - 1;
    final EntityExchangeResult<List<RequestDto>> response =
      this.requestRestClient.get(
          endpoint.id(),
          Optional.empty(),
          Optional.of(pageSize)
        );

    final List<RequestDto> fetchedRequests = Optional
      .ofNullable(response.getResponseBody())
      .orElseGet(Collections::emptyList);

    // Assert the API respected the specific page size.
    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK),
      () -> assertThat(fetchedRequests.size()).isEqualTo(pageSize)
    );
  }

  @Test
  void getRequests_respectsPage() {
    // Create an endpoint.
    final EndpointDto endpoint = this.endpointRestClient.create();

    // Create some requests.
    for (int i = 0; i < RequestConstants.MAX_REQUESTS_PER_ENDPOINT; i++) {
      this.requestRestClient.create(
          endpoint.id(),
          UUID.randomUUID().toString()
        );
    }

    // Query the requests.
    final List<RequestDto> allRequests = Optional
      .ofNullable(
        this.requestRestClient.get(
            endpoint.id(),
            Optional.empty(),
            Optional.of(RequestConstants.MAX_REQUESTS_PER_ENDPOINT)
          )
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList);

    // Fetch the requests again. But this time in two pages.
    final int newPageSize = allRequests.size() / 2;
    final List<RequestDto> firstPage = Optional
      .ofNullable(
        this.requestRestClient.get(
            endpoint.id(),
            Optional.of(0),
            Optional.of(newPageSize)
          )
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList);
    final List<RequestDto> secondPage = Optional
      .ofNullable(
        this.requestRestClient.get(
            endpoint.id(),
            Optional.of(1),
            Optional.of(newPageSize)
          )
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList);

    // Concat the first and the second page.
    final List<RequestDto> firstAndSecondPageOfRequests = new ArrayList<>();
    firstAndSecondPageOfRequests.addAll(firstPage);
    firstAndSecondPageOfRequests.addAll(secondPage);

    // Assert that the concatenation is equal to the originally fetched requests.
    assertThat(allRequests).isEqualTo(firstAndSecondPageOfRequests);
  }

  @Test
  void getRequests_returnsBadRequest_ifPageIsLessThan0() {
    final EndpointDto endpoint = this.endpointRestClient.create();

    final EntityExchangeResult<String[]> response =
      this.requestRestClient.get(
          String[].class,
          endpoint.id(),
          Optional.of(-1),
          Optional.empty()
        );

    final Optional<String[]> actualErrorMessages = Optional.ofNullable(
      response.getResponseBody()
    );

    final String[] expectedErrorMessages = new String[] {
      "The page number must be greater than or equal to 0.",
    };

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
      () ->
        assertThat(actualErrorMessages.orElse(null))
          .isEqualTo(expectedErrorMessages)
    );
  }

  @Test
  void getRequests_returnsOk_ifPageIs0() {
    final EndpointDto endpoint = this.endpointRestClient.create();

    final EntityExchangeResult<List<RequestDto>> response =
      this.requestRestClient.get(
          endpoint.id(),
          Optional.of(0),
          Optional.empty()
        );

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getRequests_returnsBadRequest_ifPageSizeIsLessThan1() {
    final EndpointDto endpoint = this.endpointRestClient.create();

    final EntityExchangeResult<String[]> response =
      this.requestRestClient.get(
          String[].class,
          endpoint.id(),
          Optional.empty(),
          Optional.of(0)
        );

    final Optional<String[]> actualErrorMessages = Optional.ofNullable(
      response.getResponseBody()
    );

    final String[] expectedErrorMessages = new String[] {
      "The page size must be between 1 and 5000.",
    };

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
      () ->
        assertThat(actualErrorMessages.orElse(null))
          .isEqualTo(expectedErrorMessages)
    );
  }

  @Test
  void getRequests_returnsOk_ifPageSizeIs1() {
    final EndpointDto endpoint = this.endpointRestClient.create();

    final EntityExchangeResult<List<RequestDto>> response =
      this.requestRestClient.get(
          endpoint.id(),
          Optional.empty(),
          Optional.of(1)
        );

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getRequests_returnsOk_ifPageSizeIsEqualTo5000() {
    final EndpointDto endpoint = this.endpointRestClient.create();

    final EntityExchangeResult<List<RequestDto>> response =
      this.requestRestClient.get(
          endpoint.id(),
          Optional.empty(),
          Optional.of(1)
        );

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getRequests_returnsBadRequest_ifPageSizeIsGreaterThan5000() {
    final EndpointDto endpoint = this.endpointRestClient.create();

    final EntityExchangeResult<String[]> response =
      this.requestRestClient.get(
          String[].class,
          endpoint.id(),
          Optional.empty(),
          Optional.of(5001)
        );

    final Optional<String[]> actualErrorMessages = Optional.ofNullable(
      response.getResponseBody()
    );

    final String[] expectedErrorMessages = new String[] {
      "The page size must be between 1 and 5000.",
    };

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
      () ->
        assertThat(actualErrorMessages.orElse(null))
          .isEqualTo(expectedErrorMessages)
    );
  }

  private static HttpHeaders getRandomHttpHeaders() {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.addAll(
      UUID.randomUUID().toString(),
      List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString())
    );
    return httpHeaders;
  }

  static String decodeBase64ToString(final String base64EncodedBody) {
    return Optional
      .ofNullable(base64EncodedBody)
      .map(Base64Utils::decodeFromString)
      .map(String::new)
      .orElse(null);
  }
}
