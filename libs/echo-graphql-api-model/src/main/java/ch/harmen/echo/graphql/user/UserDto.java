package ch.harmen.echo.graphql.user;

import java.util.Objects;

public record UserDto(String id, OwnerEndpointsConnectionDto endpoints) {
  public UserDto {
    Objects.requireNonNull(id);
  }

  public UserDto(final UserDto original) {
    this(original.id, original.endpoints);
  }
}
