package ch.harmen.echo.request;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public record Request(
  String id,
  String endpointId,
  Instant receiveTime,
  URI uri,
  HttpMethod method,
  HttpHeaders headers,
  Optional<byte[]> body
) {
  public static final Comparator<Request> COMPARATOR = Comparator
    .comparing(Request::receiveTime)
    .thenComparing(Request::id)
    .reversed();

  public Request(
    final String id,
    final String endpointId,
    final Instant receiveTime,
    final URI uri,
    final HttpMethod method,
    final HttpHeaders headers,
    final Optional<byte[]> body
  ) {
    this.id = Objects.requireNonNull(id);
    this.endpointId = Objects.requireNonNull(endpointId);
    this.receiveTime = Objects.requireNonNull(receiveTime);
    this.uri = Objects.requireNonNull(uri);
    this.method = Objects.requireNonNull(method);
    this.headers = new HttpHeaders(Objects.requireNonNull(headers));
    this.body =
      Objects.requireNonNull(body).map(it -> Arrays.copyOf(it, it.length));
  }

  public Request(final Request request) {
    this(
      request.id,
      request.endpointId,
      request.receiveTime,
      request.uri,
      request.method,
      request.headers,
      request.body
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Request request = (Request) o;
    return (
      id.equals(request.id) &&
      endpointId.equals(request.endpointId) &&
      receiveTime.equals(request.receiveTime) &&
      uri.equals(request.uri) &&
      method == request.method &&
      headers.equals(request.headers) &&
      (
        (
          body.isPresent() &&
          request.body.isPresent() &&
          Arrays.equals(body.get(), request.body.get())
        ) ||
        (body.isEmpty() && request.body.isEmpty())
      )
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      id,
      endpointId,
      receiveTime,
      uri,
      method,
      headers,
      body
    );
  }
}
