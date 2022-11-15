package ch.harmen.echo.endpoint;

public class EndpointNotFoundException extends RuntimeException {

  private final String owner;
  private final String id;

  public EndpointNotFoundException(final String owner, final String id) {
    super("Endpoint %s not found.".formatted(id));
    this.owner = owner;
    this.id = id;
  }

  public String getOwner() {
    return owner;
  }

  public String getId() {
    return id;
  }
}
