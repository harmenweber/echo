package ch.harmen.echo.endpoint;

public record Endpoint(String id, String owner, String apiKey) {
  @Override
  public String toString() {
    return "Endpoint[" + "id='" + id + '\'' + ", owner='" + owner + '\'' + ']';
  }
}
