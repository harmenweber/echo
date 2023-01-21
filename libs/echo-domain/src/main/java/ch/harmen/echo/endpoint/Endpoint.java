package ch.harmen.echo.endpoint;

import java.util.Comparator;
import java.util.Objects;

public record Endpoint(String id, String owner, String apiKey) {
  public static final Comparator<Endpoint> COMPARATOR = Comparator.comparing(
    Endpoint::id
  );

  public Endpoint {
    Objects.requireNonNull(id);
    Objects.requireNonNull(owner);
    Objects.requireNonNull(apiKey);
  }

  public Endpoint(final Endpoint endpoint) {
    this(endpoint.id, endpoint.owner, endpoint.apiKey);
  }

  @Override
  public String toString() {
    return "Endpoint[" + "id='" + id + '\'' + ", owner='" + owner + '\'' + ']';
  }
}
