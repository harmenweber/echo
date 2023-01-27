package ch.harmen.echo.endpoint;

import java.util.Objects;

public class EndpointNotFoundException extends RuntimeException {

  private final String owner;
  private final String id;

  public EndpointNotFoundException(final String owner, final String id) {
    super("Endpoint %s not found.".formatted(id));
    this.owner = Objects.requireNonNull(owner);
    this.id = Objects.requireNonNull(id);
  }

  public EndpointNotFoundException(final String id) {
    super("Endpoint %s not found.".formatted(id));
    this.id = Objects.requireNonNull(id);
    this.owner = null;
  }

  public String getOwner() {
    return owner;
  }

  public String getId() {
    return id;
  }
}
