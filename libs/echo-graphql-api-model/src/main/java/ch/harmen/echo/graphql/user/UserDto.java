package ch.harmen.echo.graphql.user;

import java.util.List;

public record UserDto(String id, List<OwnerEndpointsConnectionDto> endpoints) {
  public UserDto(final UserDto original) {
    this(original.id, original.endpoints);
  }
}
