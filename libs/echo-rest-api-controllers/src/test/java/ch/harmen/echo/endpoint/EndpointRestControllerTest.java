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
  private EndpointRestClient endpointRestClient;

  @BeforeEach
  void deleteAllExistingEndpoints() {
    Optional
      .ofNullable(
        this.endpointRestClient.get(
            Optional.of(0),
            Optional.of(EndpointConstants.MAX_ENDPOINTS_PER_OWNER)
          )
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList)
      .stream()
      .map(EndpointDto::id)
      .forEach(this.endpointRestClient::delete);
  }

  @Test
  void createEndpoint_returnsANewlyCreatedEndpoint() {
    final EndpointDto endpoint = this.endpointRestClient.create();

    assertThat(endpoint).isNotNull();
    assertAll(
      () -> assertThat(endpoint.id()).isNotBlank(),
      () -> assertThat(endpoint.owner()).isNotBlank(),
      () -> assertThat(endpoint.apiKey()).isNotBlank()
    );
  }

  @Test
  void createEndpoint_returnsDifferentEndpoints_ifCalledMultipleTimes() {
    final EndpointDto endpoint1 = this.endpointRestClient.create();
    final EndpointDto endpoint2 = this.endpointRestClient.create();

    assertAll(
      () -> assertThat(endpoint1.id()).isNotEqualTo(endpoint2.id()),
      () -> assertThat(endpoint1.owner()).isEqualTo(endpoint2.owner()),
      () -> assertThat(endpoint1.apiKey()).isNotEqualTo(endpoint2.apiKey())
    );
  }

  @Test
  void createEndpoint_returnsUnauthorizedError_ifOwnerTriesToExceedHisEndpointQuota() {
    // Create the maximum number of endpoints.
    for (int i = 0; i < EndpointConstants.MAX_ENDPOINTS_PER_OWNER; i++) {
      this.endpointRestClient.create();
    }

    // Try to create another endpoint.
    final EntityExchangeResult<String[]> response =
      this.endpointRestClient.create(String[].class);

    // Assert the API returns an UNAUTHORIZED error.
    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED),
      () ->
        assertThat(response.getResponseBody())
          .contains("You have reached your maximum number of endpoints.")
    );
  }

  @Test
  void getEndpoint() {
    final EndpointDto endpoint = this.endpointRestClient.create();

    EntityExchangeResult<EndpointDto> response =
      this.endpointRestClient.get(endpoint.id());

    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK),
      () -> assertThat(response.getResponseBody()).isEqualTo(endpoint)
    );
  }

  @Test
  void getEndpoint_returnsNotFound_ifEndpointDoesNotExist() {
    final EntityExchangeResult<String[]> response =
      this.endpointRestClient.get(String[].class, UUID.randomUUID().toString());

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete() {
    // Create and endpoint.
    final EndpointDto endpoint = this.endpointRestClient.create();

    // Fetch that endpoint.
    final EntityExchangeResult<EndpointDto> endpointLoadedBeforeDelete =
      this.endpointRestClient.get(endpoint.id());

    // Assert the endpoint exists.
    assertAll(
      () ->
        assertThat(endpointLoadedBeforeDelete.getStatus())
          .isEqualTo(HttpStatus.OK),
      () -> assertThat(endpointLoadedBeforeDelete.getResponseBody()).isNotNull()
    );

    // Delete the endpoint.
    final FluxExchangeResult<Void> deletionResponse =
      this.endpointRestClient.delete(endpoint.id());

    assertThat(deletionResponse.getStatus()).isEqualTo(HttpStatus.OK);

    // Fetch the endpoint.
    final EntityExchangeResult<String[]> endpointLoadedAfterDelete =
      this.endpointRestClient.get(String[].class, endpoint.id());

    // Assert the endpoint does not exist anymore.
    assertThat(endpointLoadedAfterDelete.getStatus())
      .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_returnsNotFound_ifEndpointDoesNotExist() {
    final FluxExchangeResult<Void> response =
      this.endpointRestClient.delete(UUID.randomUUID().toString());

    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void getEndpoints_respectsPageSize() {
    // Create the maximum number of endpoints.
    final int minNumberOfEndpoints = EndpointConstants.MAX_ENDPOINTS_PER_OWNER;
    for (int i = 0; i < minNumberOfEndpoints; i++) {
      this.endpointRestClient.create();
    }

    // Fetch endpoints with a specific page size.
    final int pageSize = minNumberOfEndpoints - 1;
    final EntityExchangeResult<List<EndpointDto>> response =
      this.endpointRestClient.get(Optional.empty(), Optional.of(pageSize));

    final List<EndpointDto> fetchedEndpoints = Optional
      .ofNullable(response.getResponseBody())
      .orElseGet(Collections::emptyList);

    // Assert the API respected the specific page size.
    assertAll(
      () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK),
      () -> assertThat(fetchedEndpoints.size()).isEqualTo(pageSize)
    );
  }

  @Test
  void getEndpoints_respectsPage() {
    // Create some endpoints.
    final int minNumberOfEndpoints = EndpointConstants.MAX_ENDPOINTS_PER_OWNER;
    for (int i = 0; i < minNumberOfEndpoints; i++) {
      this.endpointRestClient.create();
    }

    // Query the endpoints.
    final List<EndpointDto> allEndpoints = Optional
      .ofNullable(
        this.endpointRestClient.get(
            Optional.empty(),
            Optional.of(EndpointConstants.MAX_ENDPOINTS_PER_OWNER)
          )
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList);

    // Fetch the endpoints again. But this time in two pages.
    final int newPageSize = allEndpoints.size() / 2;
    final List<EndpointDto> firstPageOfEndpoints = Optional
      .ofNullable(
        this.endpointRestClient.get(Optional.of(0), Optional.of(newPageSize))
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList);
    final List<EndpointDto> secondPageOfEndpoints = Optional
      .ofNullable(
        this.endpointRestClient.get(Optional.of(1), Optional.of(newPageSize))
          .getResponseBody()
      )
      .orElseGet(Collections::emptyList);

    // Concat the first and the second page.
    final List<EndpointDto> firstAndSecondPageOfEndpoints = new ArrayList<>();
    firstAndSecondPageOfEndpoints.addAll(firstPageOfEndpoints);
    firstAndSecondPageOfEndpoints.addAll(secondPageOfEndpoints);

    // Assert that the concatenation is equal to the originally fetched endpoints.
    assertThat(allEndpoints).isEqualTo(firstAndSecondPageOfEndpoints);
  }

  @Test
  void getEndpoints_returnsBadRequest_ifPageIsLessThan0() {
    final EntityExchangeResult<String[]> response =
      this.endpointRestClient.get(
          String[].class,
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
  void getEndpoints_returnsOk_ifPageIs0() {
    final EntityExchangeResult<List<EndpointDto>> response =
      this.endpointRestClient.get(Optional.of(0), Optional.empty());

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getEndpoints_returnsBadRequest_ifPageSizeIsLessThan1() {
    final EntityExchangeResult<String[]> response =
      this.endpointRestClient.get(
          String[].class,
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
  void getEndpoints_returnsOk_ifPageSizeIs1() {
    final EntityExchangeResult<List<EndpointDto>> response =
      this.endpointRestClient.get(Optional.empty(), Optional.of(1));

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getEndpoints_returnsOk_ifPageSizeIs5000() {
    final EntityExchangeResult<List<EndpointDto>> response =
      this.endpointRestClient.get(Optional.empty(), Optional.of(5000));

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getEndpoints_returnsBadRequest_ifPageSizeIsGreaterThan5000() {
    final EntityExchangeResult<String[]> response =
      this.endpointRestClient.get(
          String[].class,
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
}
