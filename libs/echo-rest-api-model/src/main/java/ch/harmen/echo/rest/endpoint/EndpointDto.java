package ch.harmen.echo.rest.endpoint;

public record EndpointDto(String id, String owner, String apiKey) {
  public EndpointDto(final EndpointDto original) {
    this(original.id, original.owner, original.apiKey);
  }
}
