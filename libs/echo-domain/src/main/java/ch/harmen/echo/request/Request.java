package ch.harmen.echo.request;

import java.net.URI;
import java.time.Instant;
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
}
