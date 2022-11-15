package ch.harmen.echo.request;

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
) {}
