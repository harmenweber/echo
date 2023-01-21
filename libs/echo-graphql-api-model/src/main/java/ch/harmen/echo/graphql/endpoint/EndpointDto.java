package ch.harmen.echo.graphql.endpoint;

import java.util.Objects;

public record EndpointDto(
  String id,
  String apiKey,
  EndpointRequestsConnectionDto requests
) {
  public EndpointDto {
    Objects.requireNonNull(id);
    Objects.requireNonNull(apiKey);
  }

  public EndpointDto(final EndpointDto original) {
    this(original.id, original.apiKey, original.requests);
  }
}
