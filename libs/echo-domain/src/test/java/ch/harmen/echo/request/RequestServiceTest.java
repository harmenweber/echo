package ch.harmen.echo.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

final class RequestServiceTest {

  private final RequestService requestService = new RequestService(
    new RequestRepository()
  );

  private final RequestTestFixture requestTestFixture = new RequestTestFixture();

  @Test
  void create() {
    final Request request = this.requestTestFixture.create();

    final Mono<Request> createdAndLoadedRequest =
      this.requestService.create(request)
        .then(
          this.requestService.findByEndpointIdAndId(
              request.endpointId(),
              request.id()
            )
        );

    StepVerifier
      .create(createdAndLoadedRequest)
      .expectNext(request)
      .verifyComplete();
  }

  @Test
  void findByEndpointIdAndId() {
    final AtomicReference<Request> createdRequest = new AtomicReference<>();

    final Mono<Request> loadedRequest =
      this.requestService.create(this.requestTestFixture.create())
        .doOnNext(createdRequest::set)
        .flatMap(it ->
          this.requestService.findByEndpointIdAndId(it.endpointId(), it.id())
        );

    StepVerifier
      .create(loadedRequest)
      .expectNextMatches(it -> Objects.equals(it, createdRequest.get()))
      .verifyComplete();
  }

  @Test
  void findByEndpointIdAndId_returnsEmptyIfRequestDoesNotExist() {
    final String endpointId = this.requestTestFixture.getRandomEndpointId();
    final String id = this.requestTestFixture.getRandomId();

    final Mono<Request> endpoint =
      this.requestService.findByEndpointIdAndId(endpointId, id);

    StepVerifier.create(endpoint).verifyComplete();
  }

  @Test
  void getByEndpointIdAndId() {
    final AtomicReference<Request> createdRequest = new AtomicReference<>();

    final Mono<Request> loadedRequest =
      this.requestService.create(this.requestTestFixture.create())
        .doOnNext(createdRequest::set)
        .flatMap(it ->
          this.requestService.getByEndpointIdAndId(it.endpointId(), it.id())
        );

    StepVerifier
      .create(loadedRequest)
      .expectNextMatches(it -> Objects.equals(it, createdRequest.get()))
      .verifyComplete();
  }

  @Test
  void getByEndpointIdAndId_throwsException_ifRequestDoesNotExist() {
    final String endpointId = this.requestTestFixture.getRandomEndpointId();
    final String id = this.requestTestFixture.getRandomId();

    final Mono<Request> loadedRequest =
      this.requestService.getByEndpointIdAndId(endpointId, id);

    StepVerifier
      .create(loadedRequest)
      .expectErrorSatisfies(it -> {
        assertThat(it).isInstanceOf(RequestNotFoundException.class);
        if (it instanceof RequestNotFoundException exception) {
          assertAll(
            () -> assertThat(exception.getEndpointId()).isEqualTo(endpointId),
            () -> assertThat(exception.getId()).isEqualTo(id)
          );
        }
      })
      .verify();
  }

  @Test
  void delete() {
    final Request request = this.requestTestFixture.create();

    final AtomicReference<Request> foundRequestBeforeDelete = new AtomicReference<>();
    final AtomicReference<Request> foundRequestAfterDelete = new AtomicReference<>();

    final Mono<Request> createFindDeleteFindStream =
      this.requestService.create(request)
        .then(
          this.requestService.findByEndpointIdAndId(
              request.endpointId(),
              request.id()
            )
        )
        .doOnNext(foundRequestBeforeDelete::set)
        .flatMap(this.requestService::delete)
        .then(
          this.requestService.findByEndpointIdAndId(
              request.endpointId(),
              request.id()
            )
        )
        .doOnNext(foundRequestAfterDelete::set);

    StepVerifier.create(createFindDeleteFindStream).verifyComplete();

    assertAll(
      () -> assertThat(foundRequestBeforeDelete.get()).isEqualTo(request),
      () -> assertThat(foundRequestAfterDelete.get()).isNull()
    );
  }

  @Test
  void findByEndpoint_returnsRequestsOrderedByReceiveTimeDescending() {
    // Create some requests for the same endpoint.
    final String endpointId = this.requestTestFixture.getRandomEndpointId();
    Flux
      .fromStream(Stream.generate(() -> createRequestForEndpoint(endpointId)))
      .take(RequestConstants.MAX_REQUESTS_PER_ENDPOINT)
      .flatMap(this.requestService::create)
      .subscribe();

    // Find all requests by endpoint.
    final List<Request> requests =
      this.requestService.findByEndpointId(
          endpointId,
          0,
          RequestConstants.MAX_REQUESTS_PER_ENDPOINT
        )
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    // Sort the requests by receiveTime descending.
    final List<Request> sortedRequests = requests
      .stream()
      .sorted(Comparator.comparing(Request::receiveTime).reversed())
      .toList();

    // Assert the requests returned are equal to the sorted requests.
    assertThat(requests).isEqualTo(sortedRequests);
  }

  private Request createRequestForEndpoint(String endpointId) {
    return new Request(
      this.requestTestFixture.getRandomId(),
      endpointId,
      this.requestTestFixture.getRandomReceiveTime(),
      this.requestTestFixture.getRandomUri(),
      this.requestTestFixture.getRandomMethod(),
      this.requestTestFixture.getRandomHeaders(),
      Optional.of(this.requestTestFixture.getRandomBody())
    );
  }

  @Test
  void findByEndpoint_respectsPageSize() {
    // Create some requests for the same endpoint.
    final String endpointId = this.requestTestFixture.getRandomEndpointId();
    Flux
      .fromStream(Stream.generate(() -> createRequestForEndpoint(endpointId)))
      .take(RequestConstants.MAX_REQUESTS_PER_ENDPOINT)
      .flatMap(this.requestService::create)
      .subscribe();

    // Fetch the requests.
    final List<Request> allRequests =
      this.requestService.findByEndpointId(
          endpointId,
          0,
          RequestConstants.MAX_REQUESTS_PER_ENDPOINT
        )
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    // Fetch again with a different page size.
    final int newPageSize = allRequests.size() - 1;
    final List<Request> lessThanAllRequests =
      this.requestService.findByEndpointId(endpointId, 0, newPageSize)
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    assertThat(lessThanAllRequests.size()).isEqualTo(newPageSize);
  }

  @Test
  void findByEndpoint_respectsPage() {
    // Create some requests for the same endpoint.
    final String endpointId = this.requestTestFixture.getRandomEndpointId();
    Flux
      .fromStream(Stream.generate(() -> createRequestForEndpoint(endpointId)))
      .take(RequestConstants.MAX_REQUESTS_PER_ENDPOINT)
      .flatMap(this.requestService::create)
      .subscribe();

    // Fetch the requests.
    final List<Request> allRequests =
      this.requestService.findByEndpointId(
          endpointId,
          0,
          RequestConstants.MAX_REQUESTS_PER_ENDPOINT
        )
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    // Fetch the endpoints again. But this time in two pages.
    final int newPageSize = allRequests.size() / 2;
    final List<Request> firstPage =
      this.requestService.findByEndpointId(endpointId, 0, newPageSize)
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);
    final List<Request> secondPage =
      this.requestService.findByEndpointId(endpointId, 1, newPageSize)
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    // Concatenate the first with the second page.
    final ArrayList<Request> concatenatedFirstAndSecondPage = Lists.newArrayList();
    concatenatedFirstAndSecondPage.addAll(firstPage);
    concatenatedFirstAndSecondPage.addAll(secondPage);

    // Assert that the concatenation is equal to originally fetched endpoints.
    assertThat(concatenatedFirstAndSecondPage).isEqualTo(allRequests);
  }
}
