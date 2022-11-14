package ch.harmen.echo.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import ch.harmen.echo.user.CurrentUserContextSupplier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

final class EndpointServiceTest {

  private final CurrentUserContextSupplier currentUserContextSupplier = new CurrentUserContextSupplier();
  private final EndpointService endpointService = new EndpointService(
    new EndpointFactory(
      new EndpointIdFactory(),
      this.currentUserContextSupplier,
      new EndpointApiKeyFactory()
    ),
    new EndpointRepository(),
    currentUserContextSupplier
  );

  private final EndpointTestFixture endpointTestFixture = new EndpointTestFixture();

  @Test
  void create_returnsEndpointWithOwnerEqualToCurrentUser() {
    final String currentUserId = this.currentUserContextSupplier.get().id();

    final Mono<Endpoint> endpoint = this.endpointService.create();

    StepVerifier
      .create(endpoint)
      .expectNextMatches(it -> currentUserId.equals(it.owner()))
      .verifyComplete();
  }

  @Test
  void create_returnsEndpointsWithUniqueIds() {
    final int limit = EndpointConstants.MAX_ENDPOINTS_PER_OWNER;

    Mono<Integer> endpointIds = Flux
      .fromStream(Stream.generate(this.endpointService::create))
      .take(limit)
      .flatMap(Function.identity())
      .map(Endpoint::id)
      .collect(Collectors.toSet())
      .map(Set::size);

    StepVerifier.create(endpointIds).expectNext(limit).verifyComplete();
  }

  @Test
  void create_returnsEndpointsWithUniqueApiKeys() {
    final int limit = EndpointConstants.MAX_ENDPOINTS_PER_OWNER;

    Mono<Integer> endpointApiKeys = Flux
      .fromStream(Stream.generate(this.endpointService::create))
      .take(limit)
      .flatMap(Function.identity())
      .map(Endpoint::apiKey)
      .collect(Collectors.toSet())
      .map(Set::size);

    StepVerifier.create(endpointApiKeys).expectNext(limit).verifyComplete();
  }

  @Test
  void create_throwsException_ifOwnerTriesToExceedHisEndpointQuota() {
    final List<Mono<Endpoint>> endpointCreations = new ArrayList<>();
    for (int i = 0; i < EndpointConstants.MAX_ENDPOINTS_PER_OWNER; i++) {
      endpointCreations.add(this.endpointService.create());
    }

    Mono<Endpoint> attemptToExceedEndpointQuota = Flux
      .fromIterable(endpointCreations)
      .flatMap(Function.identity())
      .then(this.endpointService.create());

    StepVerifier
      .create(attemptToExceedEndpointQuota)
      .expectError(EndpointQuotaReachedException.class)
      .verify();
  }

  @Test
  void findByOwnerAndId() {
    final AtomicReference<Endpoint> createdEndpoint = new AtomicReference<>();

    final Mono<Endpoint> loadedEndpoint =
      this.endpointService.create()
        .doOnNext(createdEndpoint::set)
        .flatMap(it ->
          this.endpointService.findByOwnerAndId(it.owner(), it.id())
        );

    StepVerifier
      .create(loadedEndpoint)
      .expectNextMatches(it -> Objects.equals(it, createdEndpoint.get()))
      .verifyComplete();
  }

  @Test
  void findByOwnerAndId_returnsEmptyIfEndpointDoesNotExist() {
    final String owner = this.endpointTestFixture.getRandomOwner();
    final String id = this.endpointTestFixture.getRandomId();

    final Mono<Endpoint> endpoint =
      this.endpointService.findByOwnerAndId(owner, id);

    StepVerifier.create(endpoint).verifyComplete();
  }

  @Test
  void getByOwnerAndId() {
    final AtomicReference<Endpoint> createdEndpoint = new AtomicReference<>();

    final Mono<Endpoint> loadedEndpoint =
      this.endpointService.create()
        .doOnNext(createdEndpoint::set)
        .flatMap(it -> this.endpointService.getByOwnerAndId(it.owner(), it.id())
        );

    StepVerifier
      .create(loadedEndpoint)
      .expectNextMatches(it -> Objects.equals(it, createdEndpoint.get()))
      .verifyComplete();
  }

  @Test
  void getByOwnerAndId_throwsException_ifEndpointDoesNotExist() {
    final String owner = this.endpointTestFixture.getRandomOwner();
    final String id = this.endpointTestFixture.getRandomId();

    final Mono<Endpoint> loadedEndpoint =
      this.endpointService.getByOwnerAndId(owner, id);

    StepVerifier
      .create(loadedEndpoint)
      .expectErrorSatisfies(it -> {
        assertThat(it).isInstanceOf(EndpointNotFoundException.class);
        if (it instanceof EndpointNotFoundException exception) {
          assertAll(
            () -> assertThat(exception.getOwner()).isEqualTo(owner),
            () -> assertThat(exception.getId()).isEqualTo(id)
          );
        }
      })
      .verify();
  }

  @Test
  void delete() {
    final AtomicReference<Endpoint> endpointCreated = new AtomicReference<>();
    final AtomicReference<Endpoint> endpointLoadedBeforeDelete = new AtomicReference<>();
    final AtomicReference<Endpoint> endpointLoadedAfterDelete = new AtomicReference<>();

    // Create and capture an endpoint.
    final Mono<Endpoint> createEndpointResult =
      this.endpointService.create().doOnNext(endpointCreated::set);

    // Find and capture the endpoint.
    final Mono<Endpoint> findEndpointBeforeDeleteResult = createEndpointResult
      .flatMap(it -> this.endpointService.findByOwnerAndId(it.owner(), it.id()))
      .doOnNext(endpointLoadedBeforeDelete::set);

    // Delete the endpoint.
    final Mono<Void> deleteEndpointResult = findEndpointBeforeDeleteResult.flatMap(
      this.endpointService::delete
    );

    // Find and 'capture' the endpoint.
    final Mono<Endpoint> findEndpointAfterDeleteResult = deleteEndpointResult
      .flatMap(it -> {
        Endpoint endpointBeforeDelete = endpointLoadedBeforeDelete.get();
        return this.endpointService.findByOwnerAndId(
            endpointBeforeDelete.owner(),
            endpointBeforeDelete.id()
          );
      })
      .doOnNext(endpointLoadedAfterDelete::set);

    StepVerifier.create(findEndpointAfterDeleteResult).verifyComplete();

    assertAll(
      () ->
        assertThat(endpointLoadedBeforeDelete.get())
          .isEqualTo(endpointCreated.get()),
      () -> assertThat(endpointLoadedAfterDelete.get()).isNull()
    );
  }

  @Test
  void findByOwner_returnsEndpointsOrderedById() {
    // Create some endpoints and capture the owner.
    final String owner = Flux
      .fromStream(Stream.generate(this.endpointService::create))
      .take(EndpointConstants.MAX_ENDPOINTS_PER_OWNER)
      .flatMap(Function.identity())
      .map(Endpoint::owner)
      .blockFirst();

    // Fetch the owners endpoints.
    final List<Endpoint> endpoints =
      this.endpointService.findByOwner(
          owner,
          0,
          EndpointConstants.MAX_ENDPOINTS_PER_OWNER
        )
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    // Sort the endpoints.
    final List<Endpoint> sortedEndpoints = endpoints
      .stream()
      .sorted(Comparator.comparing(Endpoint::id))
      .toList();

    // Assert the endpoints are equal to the sorted endpoints (meaning: they were already sorted).
    assertAll(
      () -> assertThat(endpoints.size()).isGreaterThan(0),
      () -> assertThat(endpoints).isEqualTo(sortedEndpoints)
    );
  }

  @Test
  void findByOwner_respectsPageSize() {
    // Create some endpoints.
    final List<String> endpoints = Flux
      .fromStream(Stream.generate(this.endpointService::create))
      .flatMap(Function.identity())
      .take(EndpointConstants.MAX_ENDPOINTS_PER_OWNER)
      .map(Endpoint::owner)
      .collectList()
      .blockOptional()
      .orElseGet(Collections::emptyList);

    // Fetch the owner's endpoints.
    final String owner = endpoints.get(0);
    final List<Endpoint> firstFewEndpoints =
      this.endpointService.findByOwner(
          owner,
          0,
          EndpointConstants.MAX_ENDPOINTS_PER_OWNER
        )
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    // Fetch again with another page size.
    int newPageSize = firstFewEndpoints.size() - 1;
    final List<Endpoint> firstFewMinusOneEndpoints =
      this.endpointService.findByOwner(owner, 0, newPageSize)
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    // Assert the service respected the page size.
    assertThat(firstFewMinusOneEndpoints.size()).isEqualTo(newPageSize);
  }

  @Test
  void findByOwner_respectsPage() {
    // Create some endpoints.
    final List<String> createdEndpoints = Flux
      .fromStream(Stream.generate(this.endpointService::create))
      .flatMap(Function.identity())
      .take(10)
      .map(Endpoint::owner)
      .collectList()
      .blockOptional()
      .orElseGet(Collections::emptyList);

    // Fetch the owner's endpoints.
    final String owner = createdEndpoints.get(0);
    final List<Endpoint> fetchedEndpoints =
      this.endpointService.findByOwner(
          owner,
          0,
          EndpointConstants.MAX_ENDPOINTS_PER_OWNER
        )
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    // Fetch the endpoints again. But this time in two pages.
    final int newPageSize = fetchedEndpoints.size() / 2;
    final List<Endpoint> firstPage =
      this.endpointService.findByOwner(owner, 0, newPageSize)
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);
    final List<Endpoint> secondPage =
      this.endpointService.findByOwner(owner, 1, newPageSize)
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    // Concatenate the first with the second page.
    final ArrayList<Endpoint> concatenatedFirstAndSecondPage = Lists.newArrayList();
    concatenatedFirstAndSecondPage.addAll(firstPage);
    concatenatedFirstAndSecondPage.addAll(secondPage);

    // Assert that the concatenation is equal to originally fetched endpoints.
    assertThat(concatenatedFirstAndSecondPage).isEqualTo(fetchedEndpoints);
  }
}
