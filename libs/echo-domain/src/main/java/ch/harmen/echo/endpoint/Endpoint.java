package ch.harmen.echo.endpoint;

public record Endpoint(String id, String owner, String apiKey) {
  public Endpoint(final Endpoint endpoint) {
    this(endpoint.id, endpoint.owner, endpoint.apiKey);
  }

  @Override
  public String toString() {
    return "Endpoint[" + "id='" + id + '\'' + ", owner='" + owner + '\'' + ']';
  }
}
