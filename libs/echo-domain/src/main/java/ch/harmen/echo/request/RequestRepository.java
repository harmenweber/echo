package ch.harmen.echo.request;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class RequestRepository {

  private final ConcurrentMap<String, RequestList> requestsByEndpoint = new ConcurrentHashMap<>();

  Mono<Request> create(final Request request) {
    return Mono.just(request).doOnNext(this::addRequestToMap);
  }

  private void addRequestToMap(final Request request) {
    getOrCreateRequestListByEndpoint(request.endpointId()).add(request);
  }

  private RequestList getOrCreateRequestListByEndpoint(
    final String endpointId
  ) {
    return this.requestsByEndpoint.computeIfAbsent(
        endpointId,
        key -> new RequestList(RequestConstants.MAX_REQUESTS_PER_ENDPOINT)
      );
  }

  Mono<Void> delete(final Request request) {
    return Mono
      .just(request)
      .mapNotNull(this::removeRequestFromMap)
      .switchIfEmpty(
        createRequestNotFoundError(request.endpointId(), request.id())
      )
      .then();
  }

  /**
   * Removes the request from the map.
   *
   * @param request The request that must be removed.
   * @return The removed request, or {@code null} if the request was not in the map.
   */
  @Nullable
  private Request removeRequestFromMap(final Request request) {
    return getOrCreateRequestListByEndpoint(request.endpointId())
      .remove(request);
  }

  public Mono<Void> deleteByEndpointId(final String endpointId) {
    return Mono
      .just(endpointId)
      .mapNotNull(this::removeRequestsByEndpointIdFromMap)
      .then();
  }

  /**
   * Removes the request list for the given endpoint id.
   *
   * @param endpointId The endpoint id.
   * @return The removed request list, or {@code null} if there was not
   */
  @Nullable
  private RequestList removeRequestsByEndpointIdFromMap(
    final String endpointId
  ) {
    return this.requestsByEndpoint.remove(endpointId);
  }

  private static <T> Mono<T> createRequestNotFoundError(
    final String endpointId,
    final String requestId
  ) {
    return Mono.error(() -> new RequestNotFoundException(endpointId, requestId)
    );
  }

  public Flux<Request> findByEndpointId(
    final String endpointId,
    final int page,
    final int pageSize
  ) {
    return Mono
      .just(endpointId)
      .mapNotNull(this.requestsByEndpoint::get)
      .map(RequestList::getRequests)
      .flatMapMany(Flux::fromIterable)
      .skip((long) page * pageSize)
      .take(pageSize);
  }

  Mono<Request> findByEndpointIdAndId(
    final String endpointId,
    final String id
  ) {
    return Mono
      .just(endpointId)
      .mapNotNull(this.requestsByEndpoint::get)
      .map(RequestList::getRequests)
      .flatMapMany(Flux::fromIterable)
      .filter(request -> Objects.equals(request.id(), id))
      .next();
  }

  Mono<Request> getByEndpointIdAndId(final String endpointId, final String id) {
    return Mono
      .just(endpointId)
      .mapNotNull(this.requestsByEndpoint::get)
      .map(RequestList::getRequests)
      .flatMapMany(Flux::fromIterable)
      .filter(request -> Objects.equals(request.id(), id))
      .next()
      .switchIfEmpty(createRequestNotFoundError(endpointId, id));
  }

  Flux<Request> findFirstByEndpoint(
    final String endpointId,
    final int first,
    final Optional<ReceiveTimeAndId> before,
    final Optional<ReceiveTimeAndId> after
  ) {
    return Mono
      .just(endpointId)
      .mapNotNull(this.requestsByEndpoint::get)
      .flatMapMany(requestList ->
        Flux
          .fromIterable(requestList.getRequests())
          .filter(isBefore(before))
          .filter(isAfter(after))
          .sort(Request.COMPARATOR)
          .take(first)
      );
  }

  Flux<Request> findLastByEndpoint(
    final String endpointId,
    final int last,
    final Optional<ReceiveTimeAndId> before,
    final Optional<ReceiveTimeAndId> after
  ) {
    return Mono
      .just(endpointId)
      .mapNotNull(this.requestsByEndpoint::get)
      .flatMapMany(requestList ->
        Flux
          .fromIterable(requestList.getRequests())
          .filter(isBefore(before))
          .filter(isAfter(after))
          .sort(Request.COMPARATOR)
          .takeLast(last)
      );
  }

  private static Predicate<Request> isBefore(
    final Optional<ReceiveTimeAndId> optionalReceiveTimeAndId
  ) {
    return request -> requestIsBefore(request, optionalReceiveTimeAndId);
  }

  private static boolean requestIsBefore(
    final Request request,
    final Optional<ReceiveTimeAndId> optionalReceiveTimeAndId
  ) {
    return optionalReceiveTimeAndId
      .map(receiveTimeAndId -> requestIsBefore(request, receiveTimeAndId))
      .orElse(true);
  }

  private static boolean requestIsBefore(
    final Request request,
    final ReceiveTimeAndId receiveTimeAndId
  ) {
    return (
      ReceiveTimeAndId.COMPARATOR.compare(
        new ReceiveTimeAndId(request.receiveTime(), request.id()),
        receiveTimeAndId
      ) <
      0
    );
  }

  private static Predicate<Request> isAfter(
    final Optional<ReceiveTimeAndId> optionalReceiveTimeAndId
  ) {
    return request -> requestIsAfter(request, optionalReceiveTimeAndId);
  }

  private static boolean requestIsAfter(
    final Request request,
    final Optional<ReceiveTimeAndId> optionalReceiveTimeAndId
  ) {
    return optionalReceiveTimeAndId
      .map(receiveTimeAndId -> requestIsAfter(request, receiveTimeAndId))
      .orElse(true);
  }

  private static boolean requestIsAfter(
    final Request request,
    final ReceiveTimeAndId receiveTimeAndId
  ) {
    return (
      ReceiveTimeAndId.COMPARATOR.compare(
        new ReceiveTimeAndId(request.receiveTime(), request.id()),
        receiveTimeAndId
      ) >
      0
    );
  }
}
