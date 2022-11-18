package ch.harmen.echo.graphql.user;

public record UserDto(String id, OwnerEndpointsConnectionDto endpoints) {
  public UserDto(final UserDto original) {
    this(original.id, original.endpoints);
  }
}
