package ch.harmen.echo.endpoint;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class EndpointRepository {

  private final Map<String, Map<String, Endpoint>> endpointsByOwner = new HashMap<>();

  Mono<Endpoint> save(final Endpoint endpoint) {
    return Mono.just(endpoint).doOnNext(this::addEndpointToMap);
  }

  private void addEndpointToMap(Endpoint it) {
    this.endpointsByOwner.compute(
        it.owner(),
        (key, value) -> {
          final Map<String, Endpoint> endpoints = Optional
            .ofNullable(value)
            .orElseGet(HashMap::new);
          endpoints.put(it.id(), it);
          return endpoints;
        }
      );
  }

  Mono<Void> delete(final Endpoint endpoint) {
    return Mono
      .just(endpoint.owner())
      .mapNotNull(this.endpointsByOwner::get)
      .switchIfEmpty(
        createEndpointNotFoundError(endpoint.owner(), endpoint.id())
      )
      .map(endpoints -> endpoints.remove(endpoint.id()))
      .then();
  }

  private static <T> Mono<T> createEndpointNotFoundError(
    final String owner,
    final String id
  ) {
    return Mono.error(() -> new EndpointNotFoundException(owner, id));
  }

  public Flux<Endpoint> findByOwner(
    final String owner,
    final int page,
    final int pageSize
  ) {
    return Mono
      .just(owner)
      .mapNotNull(this.endpointsByOwner::get)
      .flatMapMany(endpoints ->
        Flux
          .fromIterable(endpoints.values())
          .sort(Comparator.comparing(Endpoint::id))
          .skip((long) page * pageSize)
          .take(pageSize)
      );
  }

  Mono<Endpoint> findByOwnerAndId(final String owner, final String id) {
    return Mono
      .just(owner)
      .mapNotNull(this.endpointsByOwner::get)
      .mapNotNull(endpoints -> endpoints.get(id));
  }

  Mono<Endpoint> getByOwnerAndId(final String owner, final String id) {
    return Mono
      .just(owner)
      .mapNotNull(this.endpointsByOwner::get)
      .mapNotNull(endpoints -> endpoints.get(id))
      .switchIfEmpty(createEndpointNotFoundError(owner, id));
  }

  Mono<Integer> countByOwner(final String owner) {
    return Mono
      .just(owner)
      .mapNotNull(this.endpointsByOwner::get)
      .map(Map::size)
      .defaultIfEmpty(0);
  }
}
