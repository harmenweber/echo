package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.graphql.request.RequestHeaderDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record EndpointRequestDto(
  String id,
  String receiveTime,
  String uri,
  String method,
  List<RequestHeaderDto> headers,
  Optional<String> base64EncodedBody
) {
  public EndpointRequestDto(
    final String id,
    final String receiveTime,
    final String uri,
    final String method,
    final List<RequestHeaderDto> headers,
    final Optional<String> base64EncodedBody
  ) {
    this.id = Objects.requireNonNull(id);
    this.receiveTime = Objects.requireNonNull(receiveTime);
    this.uri = Objects.requireNonNull(uri);
    this.method = Objects.requireNonNull(method);
    this.headers = new ArrayList<>(Objects.requireNonNull(headers));
    this.base64EncodedBody = Objects.requireNonNull(base64EncodedBody);
  }

  public EndpointRequestDto(final EndpointRequestDto original) {
    this(
      original.id,
      original.receiveTime,
      original.uri,
      original.method,
      original.headers,
      original.base64EncodedBody
    );
  }
}
