package ch.harmen.echo.endpoint;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class EndpointRepository {

  private final ConcurrentMap<String, Endpoint> endpointsById = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, ConcurrentMap<String, Endpoint>> endpointsByOwner = new ConcurrentHashMap<>();

  Mono<Endpoint> create(final Endpoint endpoint) {
    return Mono.just(endpoint).doOnNext(this::addEndpointToMaps);
  }

  private void addEndpointToMaps(final Endpoint endpoint) {
    this.endpointsById.put(endpoint.id(), endpoint);
    final String owner = endpoint.owner();
    this.endpointsByOwner.putIfAbsent(owner, new ConcurrentHashMap<>());
    this.endpointsByOwner.get(owner).put(endpoint.id(), endpoint);
  }

  Mono<Void> delete(final Endpoint endpoint) {
    return Mono
      .just(endpoint)
      .mapNotNull(this::removeEndpointFromMaps)
      .switchIfEmpty(
        createEndpointNotFoundError(endpoint.owner(), endpoint.id())
      )
      .then();
  }

  /**
   * Removes the endpoint from the maps.
   *
   * @param endpoint The endpoint that must be removed.
   * @return The removed endpoint, or {@code null} if the endpoint was not in the maps.
   */
  private Endpoint removeEndpointFromMaps(final Endpoint endpoint) {
    this.endpointsById.remove(endpoint.id());
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

  private static <T> Mono<T> createEndpointNotFoundError(final String id) {
    return Mono.error(() -> new EndpointNotFoundException(id));
  }

  Flux<Endpoint> findByOwner(
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

  Flux<Endpoint> findFirstByOwner(
    final String owner,
    final int first,
    final Optional<String> before,
    final Optional<String> after
  ) {
    return Mono
      .just(owner)
      .mapNotNull(this.endpointsByOwner::get)
      .flatMapMany(endpoints ->
        Flux
          .fromIterable(endpoints.values())
          .filter(isBefore(before))
          .filter(isAfter(after))
          .sort(Endpoint.COMPARATOR)
          .take(first)
      );
  }

  Flux<Endpoint> findLastByOwner(
    final String owner,
    final int last,
    final Optional<String> before,
    final Optional<String> after
  ) {
    return Mono
      .just(owner)
      .mapNotNull(this.endpointsByOwner::get)
      .flatMapMany(endpoints ->
        Flux
          .fromIterable(endpoints.values())
          .filter(isBefore(before))
          .filter(isAfter(after))
          .sort(Comparator.comparing(Endpoint::id))
          .takeLast(last)
      );
  }

  private static Predicate<Endpoint> isBefore(
    final Optional<String> optionalId
  ) {
    return endpoint -> endpointIdIsBefore(endpoint, optionalId);
  }

  private static boolean endpointIdIsBefore(
    final Endpoint endpoint,
    final Optional<String> optionalId
  ) {
    return optionalId.map(id -> endpointIdIsBefore(endpoint, id)).orElse(true);
  }

  private static boolean endpointIdIsBefore(
    final Endpoint endpoint,
    final String id
  ) {
    return id.compareTo(endpoint.id()) > 0;
  }

  private static Predicate<Endpoint> isAfter(
    final Optional<String> optionalId
  ) {
    return endpoint -> endpointIdIsAfter(endpoint, optionalId);
  }

  private static boolean endpointIdIsAfter(
    final Endpoint endpoint,
    final Optional<String> optionalId
  ) {
    return optionalId.map(id -> endpointIdIsAfter(endpoint, id)).orElse(true);
  }

  private static boolean endpointIdIsAfter(
    final Endpoint endpoint,
    final String id
  ) {
    return id.compareTo(endpoint.id()) < 0;
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

  Mono<Endpoint> getById(final String id) {
    return Mono
      .just(id)
      .mapNotNull(this.endpointsById::get)
      .switchIfEmpty(createEndpointNotFoundError(id));
  }

  Mono<Integer> countByOwner(final String owner) {
    return Mono
      .just(owner)
      .mapNotNull(this.endpointsByOwner::get)
      .map(Map::size)
      .defaultIfEmpty(0);
  }
}
