package ch.harmen.echo.endpoint;

import ch.harmen.echo.user.CurrentUserContextSupplier;
import java.util.Objects;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EndpointService {

  private final EndpointFactory endpointFactory;
  private final EndpointRepository endpointRepository;
  private final CurrentUserContextSupplier currentUserContextSupplier;

  public EndpointService(
    final EndpointFactory endpointFactory,
    final EndpointRepository endpointRepository,
    final CurrentUserContextSupplier currentUserContextSupplier
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
      .flatMap(this.endpointRepository::create);
  }

  private static void assertEndpointQuoteNotReachedYet(
    String owner,
    int endpointCount
  ) {
    if (endpointCount >= EndpointConstants.MAX_ENDPOINTS_PER_OWNER) {
      throw new EndpointQuotaReachedException(
        owner,
        EndpointConstants.MAX_ENDPOINTS_PER_OWNER,
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

  private static void assertPageParameter(final int page) {
    Assert.isTrue(
      page >= 0,
      () -> "Parameter page must be >= 0 but was %d".formatted(page)
    );
  }

  private static void assertPageSizeParameter(final int pageSize) {
    Assert.isTrue(
      pageSize >= 1,
      () -> "Parameter pageSize must be >= 0 but was %d".formatted(pageSize)
    );
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
