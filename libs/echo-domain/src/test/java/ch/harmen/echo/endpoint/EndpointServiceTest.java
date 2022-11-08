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
import org.junit.jupiter.api.BeforeEach;
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

  @BeforeEach
  void deleteAllExistingEndpoints() {
    String currentUserId = this.currentUserContextSupplier.get().id();
    Flux<Void> deletions =
      this.endpointService.findByOwner(
          currentUserId,
          0,
          EndpointService.MAX_ENDPOINTS_PER_OWNER
        )
        .flatMap(this.endpointService::delete);
    StepVerifier.create(deletions).verifyComplete();
  }

  @Test
  void create_returnsEndpointWithOwnerEqualToCurrentUser() {
    String currentUserId = this.currentUserContextSupplier.get().id();

    final Mono<Endpoint> endpoint = this.endpointService.create();

    StepVerifier
      .create(endpoint)
      .expectNextMatches(it -> currentUserId.equals(it.owner()))
      .verifyComplete();
  }

  @Test
  void create_returnsEndpointsWithUniqueIds() {
    final int limit = EndpointService.MAX_ENDPOINTS_PER_OWNER;

    Mono<Integer> endpointIds = Flux
      .fromStream(Stream.generate(this.endpointService::create).limit(limit))
      .flatMap(Function.identity())
      .map(Endpoint::id)
      .collect(Collectors.toSet())
      .map(Set::size);

    StepVerifier.create(endpointIds).expectNext(limit).verifyComplete();
  }

  @Test
  void create_returnsEndpointsWithUniqueApiKeys() {
    final int limit = EndpointService.MAX_ENDPOINTS_PER_OWNER;

    Mono<Integer> endpointApiKeys = Flux
      .fromStream(Stream.generate(this.endpointService::create).limit(limit))
      .flatMap(Function.identity())
      .map(Endpoint::apiKey)
      .collect(Collectors.toSet())
      .map(Set::size);

    StepVerifier.create(endpointApiKeys).expectNext(limit).verifyComplete();
  }

  @Test
  void create_throwsException_ifOwnerTriesToExceedHisEndpointQuota() {
    final List<Mono<Endpoint>> endpointCreations = new ArrayList<>();
    for (int i = 0; i < EndpointService.MAX_ENDPOINTS_PER_OWNER; i++) {
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

    final Mono<Endpoint> endpoint =
      this.endpointService.create().doOnNext(createdEndpoint::set);

    final Mono<Endpoint> loadedEndpoint = endpoint.flatMap(it ->
      this.endpointService.findByOwnerAndId(it.owner(), it.id())
    );

    StepVerifier
      .create(loadedEndpoint)
      .expectNextMatches(it -> Objects.equals(it, createdEndpoint.get()))
      .verifyComplete();
  }

  @Test
  void findByOwnerAndId_returnsEmptyIfEndpointDoesNotExist() {
    final Mono<Endpoint> endpoint =
      this.endpointService.findByOwnerAndId(
          this.endpointTestFixture.getRandomOwner(),
          this.endpointTestFixture.getRandomId()
        );

    StepVerifier.create(endpoint).verifyComplete();
  }

  @Test
  void delete() {
    final AtomicReference<Endpoint> endpointCreated = new AtomicReference<>();
    final AtomicReference<Endpoint> endpointLoadedBeforeDelete = new AtomicReference<>();
    final AtomicReference<Endpoint> endpointLoadedAfterDelete = new AtomicReference<>();

    final Mono<Endpoint> createEndpointResult =
      this.endpointService.create().doOnNext(endpointCreated::set);

    final Mono<Endpoint> findEndpointBeforeDeleteResult = createEndpointResult
      .flatMap(it -> this.endpointService.findByOwnerAndId(it.owner(), it.id()))
      .doOnNext(endpointLoadedBeforeDelete::set);

    final Mono<Void> deleteEndpointResult = findEndpointBeforeDeleteResult.flatMap(
      this.endpointService::delete
    );

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
    // We create 10 endpoints and capture the owner.
    final String owner = Flux
      .<Mono<Endpoint>>generate(sink -> sink.next(this.endpointService.create())
      )
      .take(10)
      .flatMap(Function.identity())
      .map(Endpoint::owner)
      .blockFirst();

    final List<Endpoint> firstHundredEndpoints =
      this.endpointService.findByOwner(
          owner,
          0,
          EndpointService.MAX_ENDPOINTS_PER_OWNER
        )
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    final List<Endpoint> sortedFirstHundredEndpoints = firstHundredEndpoints
      .stream()
      .sorted(Comparator.comparing(Endpoint::id))
      .toList();

    assertAll(
      () -> assertThat(firstHundredEndpoints.size()).isGreaterThan(0),
      () ->
        assertThat(firstHundredEndpoints).isEqualTo(sortedFirstHundredEndpoints)
    );
  }

  @Test
  void findByOwner_respectsPageSize() {
    // We create 10 endpoints and capture the owner.
    final List<String> endpoints = Flux
      .fromStream(Stream.generate(this.endpointService::create))
      .flatMap(Function.identity())
      .take(10)
      .map(Endpoint::owner)
      .collectList()
      .blockOptional()
      .orElseGet(Collections::emptyList);

    final String owner = endpoints.get(0);

    final List<Endpoint> firstFewEndpoints =
      this.endpointService.findByOwner(
          owner,
          0,
          EndpointService.MAX_ENDPOINTS_PER_OWNER
        )
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    int newPageSize = firstFewEndpoints.size() - 1;

    final List<Endpoint> firstFewMinusOneEndpoints =
      this.endpointService.findByOwner(owner, 0, newPageSize)
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    assertThat(firstFewMinusOneEndpoints.size()).isEqualTo(newPageSize);
  }

  @Test
  void findByOwner_respectsPage() {
    // We create 10 endpoints and capture the owner.
    final List<String> endpoints = Flux
      .fromStream(Stream.generate(this.endpointService::create))
      .flatMap(Function.identity())
      .take(10)
      .map(Endpoint::owner)
      .collectList()
      .blockOptional()
      .orElseGet(Collections::emptyList);

    final String owner = endpoints.get(0);

    final List<Endpoint> firstFewEndpoints =
      this.endpointService.findByOwner(
          owner,
          0,
          EndpointService.MAX_ENDPOINTS_PER_OWNER
        )
        .collectList()
        .blockOptional()
        .orElseGet(Collections::emptyList);

    final int newPageSize = firstFewEndpoints.size() / 2;

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

    ArrayList<Endpoint> concatenatedFirstAndSecondPage = Lists.newArrayList();
    concatenatedFirstAndSecondPage.addAll(firstPage);
    concatenatedFirstAndSecondPage.addAll(secondPage);

    assertThat(concatenatedFirstAndSecondPage).isEqualTo(firstFewEndpoints);
  }
}
