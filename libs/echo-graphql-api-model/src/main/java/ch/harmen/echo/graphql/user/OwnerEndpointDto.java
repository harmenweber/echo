package ch.harmen.echo.graphql.user;

import java.util.Objects;

public record OwnerEndpointDto(String id, String apiKey) {
  public OwnerEndpointDto {
    Objects.requireNonNull(id);
    Objects.requireNonNull(apiKey);
  }

  public OwnerEndpointDto(final OwnerEndpointDto original) {
    this(original.id, original.apiKey);
  }
}
