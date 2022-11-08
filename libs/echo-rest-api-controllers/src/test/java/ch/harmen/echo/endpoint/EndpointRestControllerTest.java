package ch.harmen.echo.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import ch.harmen.echo.TestConfiguration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;

@SpringBootTest(classes = TestConfiguration.class)
public class EndpointRestControllerTest {

  @Autowired
  private EndpointRestClient restClient;

  @BeforeEach
  void deleteAllExistingEndpoints() {
    Optional
      .ofNullable(
        this.restClient.getEndpoints(
            Optional.of(0),
            Optional.of(EndpointService.MAX_ENDPOINTS_PER_OWNER)
          )
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList)
      .forEach(endpoint -> this.restClient.deleteEndpoint(endpoint.id()));
  }

  @Test
  void createEndpoint_returnsANewlyCreatedEndpoint() {
    final EndpointDto endpoint = this.restClient.createEndpoint();

    assertThat(endpoint).isNotNull();
    assertAll(
      () -> assertThat(endpoint.id()).isNotBlank(),
      () -> assertThat(endpoint.owner()).isNotBlank(),
      () -> assertThat(endpoint.apiKey()).isNotBlank()
    );
  }

  @Test
  void createEndpoint_returnsDifferentEndpoints_ifCalledMultipleTimes() {
    final EndpointDto endpoint1 = this.restClient.createEndpoint();
    final EndpointDto endpoint2 = this.restClient.createEndpoint();

    assertAll(
      () -> assertThat(endpoint1.id()).isNotEqualTo(endpoint2.id()),
      () -> assertThat(endpoint1.owner()).isEqualTo(endpoint2.owner()),
      () -> assertThat(endpoint1.apiKey()).isNotEqualTo(endpoint2.apiKey())
    );
  }

  @Test
  void createEndpoint_returnsUnauthorizedError_ifOwnerTriesToExceedHisEndpointQuota() {
    for (int i = 0; i < EndpointService.MAX_ENDPOINTS_PER_OWNER; i++) {
      this.restClient.createEndpoint();
    }

    final EntityExchangeResult<String[]> response =
      this.restClient.createEndpoint(String[].class);

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED),
      () ->
        assertThat(response.getResponseBody())
          .contains("You have reached your maximum number of endpoints.")
    );
  }

  @Test
  void getEndpoint() {
    final EndpointDto endpoint = this.restClient.createEndpoint();

    assertThat(endpoint).isNotNull();

    EntityExchangeResult<EndpointDto> response =
      this.restClient.getEndpoint(endpoint.id());

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK),
      () -> assertThat(response.getResponseBody()).isEqualTo(endpoint)
    );
  }

  @Test
  void getEndpoint_returnsNotFound_ifEndpointDoesNotExist() {
    EntityExchangeResult<EndpointDto> response =
      this.restClient.getEndpoint(UUID.randomUUID().toString());
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete() {
    final EndpointDto endpoint = this.restClient.createEndpoint();

    final EntityExchangeResult<EndpointDto> endpointLoadedBeforeDelete =
      this.restClient.getEndpoint(endpoint.id());

    assertAll(
      () ->
        assertThat(endpointLoadedBeforeDelete.getStatus())
          .isEqualTo(HttpStatus.OK),
      () -> assertThat(endpointLoadedBeforeDelete.getResponseBody()).isNotNull()
    );

    final FluxExchangeResult<Void> deletionResponse =
      this.restClient.deleteEndpoint(endpoint.id());

    assertThat(deletionResponse.getStatus()).isEqualTo(HttpStatus.OK);

    final EntityExchangeResult<EndpointDto> endpointLoadedAfterDelete =
      this.restClient.getEndpoint(endpoint.id());

    assertThat(endpointLoadedAfterDelete.getStatus())
      .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_returnsNotFound_ifEndpointDoesNotExist() {
    FluxExchangeResult<Void> response =
      this.restClient.deleteEndpoint(UUID.randomUUID().toString());

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void getEndpoints_respectsPageSize() {
    final int minNumberOfEndpoints = 10;
    for (int i = 0; i < minNumberOfEndpoints; i++) {
      this.restClient.createEndpoint();
    }

    final int pageSize = minNumberOfEndpoints - 1;

    final EntityExchangeResult<List<EndpointDto>> response =
      this.restClient.getEndpoints(Optional.empty(), Optional.of(pageSize));

    final List<EndpointDto> fetchedEndpoints = Optional
      .ofNullable(response.getResponseBody())
      .orElseGet(Collections::emptyList);

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK),
      () -> assertThat(fetchedEndpoints.size()).isEqualTo(pageSize)
    );
  }

  @Test
  void getEndpoints_respectsPage() {
    // Create some endpoints.
    final int minNumberOfEndpoints = 10;
    for (int i = 0; i < minNumberOfEndpoints; i++) {
      this.restClient.createEndpoint();
    }

    // Query up to the first 100 endpoints.
    final List<EndpointDto> firstFewEndpoints = Optional
      .ofNullable(
        this.restClient.getEndpoints(Optional.empty(), Optional.of(100))
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList);

    // From the list of first few endpoints, calculate a page size that guarantees two pages.
    final int newPageSize = firstFewEndpoints.size() / 2;

    // Query the first and the second page.
    final List<EndpointDto> firstPageOfEndpoints = Optional
      .ofNullable(
        this.restClient.getEndpoints(Optional.of(0), Optional.of(newPageSize))
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList);
    final List<EndpointDto> secondPageOfEndpoints = Optional
      .ofNullable(
        this.restClient.getEndpoints(Optional.of(1), Optional.of(newPageSize))
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList);

    // Concat the first and the second page.
    final List<EndpointDto> firstAndSecondPageOfEndpoints = new ArrayList<>();
    firstAndSecondPageOfEndpoints.addAll(firstPageOfEndpoints);
    firstAndSecondPageOfEndpoints.addAll(secondPageOfEndpoints);

    // Assert that the concatenation of the first and the second page is equal to the initial query that fetched both pages at once.
    assertThat(firstFewEndpoints).isEqualTo(firstAndSecondPageOfEndpoints);
  }

  @Test
  void getEndpoints_returnsBadRequest_ifPageIsLessThan0() {
    EntityExchangeResult<String[]> response =
      this.restClient.getEndpoints(
          String[].class,
          Optional.of(-1),
          Optional.empty()
        );

    Optional<String[]> actualErrorMessages = Optional.ofNullable(
      response.getResponseBody()
    );

    String[] expectedErrorMessages = new String[] {
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
  void getEndpoints_returnsOk_ifPageIs0() {
    EntityExchangeResult<List<EndpointDto>> response =
      this.restClient.getEndpoints(Optional.of(0), Optional.empty());

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getEndpoints_returnsBadRequest_ifPageSizeIsLessThan1() {
    EntityExchangeResult<String[]> response =
      this.restClient.getEndpoints(
          String[].class,
          Optional.empty(),
          Optional.of(0)
        );

    Optional<String[]> actualErrorMessages = Optional.ofNullable(
      response.getResponseBody()
    );

    String[] expectedErrorMessages = new String[] {
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
  void getEndpoints_returnsOk_ifPageSizeIs1() {
    EntityExchangeResult<List<EndpointDto>> response =
      this.restClient.getEndpoints(Optional.empty(), Optional.of(1));

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getEndpoints_returnsOk_ifPageSizeIs5000() {
    EntityExchangeResult<List<EndpointDto>> response =
      this.restClient.getEndpoints(Optional.empty(), Optional.of(5000));

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getEndpoints_returnsBadRequest_ifPageSizeIsGreaterThan5000() {
    EntityExchangeResult<String[]> response =
      this.restClient.getEndpoints(
          String[].class,
          Optional.empty(),
          Optional.of(5001)
        );

    Optional<String[]> actualErrorMessages = Optional.ofNullable(
      response.getResponseBody()
    );

    String[] expectedErrorMessages = new String[] {
      "The page size must be between 1 and 5000.",
    };

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST),
      () ->
        assertThat(actualErrorMessages.orElse(null))
          .isEqualTo(expectedErrorMessages)
    );
  }
}
