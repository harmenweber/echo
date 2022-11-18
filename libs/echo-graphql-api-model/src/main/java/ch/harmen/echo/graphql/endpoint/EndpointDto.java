package ch.harmen.echo.graphql.endpoint;

public record EndpointDto(String id, String apiKey) {
  public EndpointDto(final EndpointDto original) {
    this(original.id, original.apiKey);
  }
}
