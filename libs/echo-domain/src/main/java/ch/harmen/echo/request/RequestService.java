package ch.harmen.echo.request;

import java.util.Objects;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RequestService {

  private final RequestRepository requestRepository;

  public RequestService(final RequestRepository requestRepository) {
    this.requestRepository = Objects.requireNonNull(requestRepository);
  }

  public Mono<Request> create(final Request request) {
    Objects.requireNonNull(request);
    return this.requestRepository.create(request);
  }

  public Mono<Void> delete(final Request request) {
    Objects.requireNonNull(request);
    return this.requestRepository.delete(request);
  }

  public Flux<Request> findByEndpointId(
    final String endpointId,
    final int page,
    final int pageSize
  ) {
    Objects.requireNonNull(endpointId);
    assertPageParameter(page);
    assertPageSizeParameter(pageSize);
    return this.requestRepository.findByEndpointId(endpointId, page, pageSize);
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

  public Mono<Request> findByEndpointIdAndId(
    final String endpointId,
    final String id
  ) {
    Objects.requireNonNull(endpointId);
    Objects.requireNonNull(id);
    return this.requestRepository.findByEndpointIdAndId(endpointId, id);
  }

  public Mono<Request> getByEndpointIdAndId(
    final String endpointId,
    final String id
  ) {
    Objects.requireNonNull(endpointId);
    Objects.requireNonNull(id);
    return this.requestRepository.getByEndpointIdAndId(endpointId, id);
  }
}
