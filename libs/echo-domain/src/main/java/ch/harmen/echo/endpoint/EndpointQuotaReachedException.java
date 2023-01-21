package ch.harmen.echo.endpoint;

import java.util.Objects;

public class EndpointQuotaReachedException extends RuntimeException {

  private final String owner;
  private final int endpointQuota;
  private final int endpointCount;

  EndpointQuotaReachedException(
    final String owner,
    final int endpointQuota,
    final int endpointCount
  ) {
    super("You have reached your maximum number of endpoints.");
    this.owner = Objects.requireNonNull(owner);
    this.endpointQuota = endpointQuota;
    this.endpointCount = endpointCount;
  }

  public String getOwner() {
    return owner;
  }

  public int getEndpointQuota() {
    return endpointQuota;
  }

  public int getEndpointCount() {
    return endpointCount;
  }
}
