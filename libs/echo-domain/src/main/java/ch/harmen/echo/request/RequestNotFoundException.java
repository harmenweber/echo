package ch.harmen.echo.request;

import java.util.Objects;

public final class RequestNotFoundException extends RuntimeException {

  private final String endpointId;
  private final String id;

  public RequestNotFoundException(final String endpointId, final String id) {
    super("Request %s of endpoint %s not found.".formatted(id, endpointId));
    this.endpointId = Objects.requireNonNull(endpointId);
    this.id = Objects.requireNonNull(id);
  }

  public String getEndpointId() {
    return endpointId;
  }

  public String getId() {
    return id;
  }
}
