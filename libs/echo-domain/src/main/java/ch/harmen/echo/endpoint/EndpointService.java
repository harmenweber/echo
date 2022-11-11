package ch.harmen.echo.endpoint;

import ch.harmen.echo.user.CurrentUserContextSupplier;
import java.util.Objects;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EndpointService {

  static final int MAX_ENDPOINTS_PER_OWNER = 10;

  private final EndpointFactory endpointFactory;
  private final EndpointRepository endpointRepository;
  private final CurrentUserContextSupplier currentUserContextSupplier;

  public EndpointService(
    EndpointFactory endpointFactory,
    EndpointRepository endpointRepository,
    CurrentUserContextSupplier currentUserContextSupplier
  ) {
    this.endpointFactory = Objects.requireNonNull(endpointFactory);
    this.endpointRepository = Objects.requireNonNull(endpointRepository);
    this.currentUserContextSupplier =
      Objects.requireNonNull(currentUserContextSupplier);
  }

  public Mono<Endpoint> create() {
    return Mono
      .just(this.currentUserContextSupplier.get().id())
      .zipWhen(this.endpointRepository::countByOwner)
      .doOnNext(ownerAndEndpointCountTuple -> {
        assertEndpointQuoteNotReachedYet(
          ownerAndEndpointCountTuple.getT1(),
          ownerAndEndpointCountTuple.getT2()
        );
      })
      .then(Mono.fromSupplier(this.endpointFactory::create))
      .flatMap(this.endpointRepository::save);
  }

  private static void assertEndpointQuoteNotReachedYet(
    String owner,
    int endpointCount
  ) {
    if (endpointCount >= MAX_ENDPOINTS_PER_OWNER) {
      throw new EndpointQuotaReachedException(
        owner,
        MAX_ENDPOINTS_PER_OWNER,
        endpointCount
      );
    }
  }

  public Mono<Void> delete(final Endpoint endpoint) {
    Objects.requireNonNull(endpoint);
    return this.endpointRepository.delete(endpoint);
  }

  public Flux<Endpoint> findByOwner(
    final String owner,
    final int page,
    final int pageSize
  ) {
    Objects.requireNonNull(owner);
    assertPageParameter(page);
    assertPageSizeParameter(pageSize);
    return this.endpointRepository.findByOwner(owner, page, pageSize);
  }

  private static void assertPageParameter(int page) {
    if (page < 0) {
      throw new IllegalArgumentException(
        "Parameter page must be >= 0 but was %d".formatted(page)
      );
    }
  }

  private static void assertPageSizeParameter(int pageSize) {
    if (pageSize < 1) {
      throw new IllegalArgumentException(
        "Parameter pageSize must be >= 0 but was %d".formatted(pageSize)
      );
    }
  }

  public Mono<Endpoint> findByOwnerAndId(final String owner, final String id) {
    Objects.requireNonNull(owner);
    Objects.requireNonNull(id);
    return this.endpointRepository.findByOwnerAndId(owner, id);
  }

  public Mono<Endpoint> getByOwnerAndId(final String owner, final String id) {
    Objects.requireNonNull(owner);
    Objects.requireNonNull(id);
    return this.endpointRepository.getByOwnerAndId(owner, id);
  }
}
