package ch.harmen.echo.request;

public class RequestNotFoundException extends RuntimeException {

  private final String endpointId;
  private final String id;

  public RequestNotFoundException(final String endpointId, final String id) {
    super("Request %s of endpoint %s not found.".formatted(id, endpointId));
    this.endpointId = endpointId;
    this.id = id;
  }

  public String getEndpointId() {
    return endpointId;
  }

  public String getId() {
    return id;
  }
}
