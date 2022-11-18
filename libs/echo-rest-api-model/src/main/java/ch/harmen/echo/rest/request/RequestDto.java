package ch.harmen.echo.rest.request;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record RequestDto(
  String id,
  String endpointId,
  Instant receiveTime,
  URI uri,
  String method,
  Map<String, List<String>> headers,
  Optional<String> base64EncodedBody
) {
  public RequestDto(final RequestDto original) {
    this(
      original.id,
      original.endpointId,
      original.receiveTime,
      original.uri,
      original.method,
      original.headers,
      original.base64EncodedBody
    );
  }
}
