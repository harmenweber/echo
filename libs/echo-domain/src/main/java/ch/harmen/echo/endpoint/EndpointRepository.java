package ch.harmen.echo.endpoint;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class EndpointRepository {

  private final ConcurrentMap<String, ConcurrentMap<String, Endpoint>> endpointsByOwner = new ConcurrentHashMap<>();

  Mono<Endpoint> save(final Endpoint endpoint) {
    return Mono.just(endpoint).doOnNext(this::addEndpointToMap);
  }

  private void addEndpointToMap(final Endpoint endpoint) {
    final String owner = endpoint.owner();
    this.endpointsByOwner.putIfAbsent(owner, new ConcurrentHashMap<>());
    this.endpointsByOwner.get(owner).put(endpoint.id(), endpoint);
  }

  Mono<Void> delete(final Endpoint endpoint) {
    return Mono
      .just(endpoint)
      .mapNotNull(this::removeEndpointFromMap)
      .switchIfEmpty(
        createEndpointNotFoundError(endpoint.owner(), endpoint.id())
      )
      .then();
  }

  /**
   * Removes the endpoint from the map.
   *
   * @param endpoint The endpoint that must be removed.
   * @return The removed endpoint, or {@code null} if the endpoint was not in the map.
   */
  private Endpoint removeEndpointFromMap(final Endpoint endpoint) {
    final String owner = endpoint.owner();
    this.endpointsByOwner.putIfAbsent(owner, new ConcurrentHashMap<>());
    return this.endpointsByOwner.get(owner).remove(endpoint.id());
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
