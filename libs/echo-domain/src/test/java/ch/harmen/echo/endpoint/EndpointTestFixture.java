package ch.harmen.echo.endpoint;

import java.util.UUID;

public final class EndpointTestFixture {

  public Endpoint create() {
    return new Endpoint(getRandomId(), getRandomOwner(), getRandomApiKey());
  }

  public String getRandomId() {
    return UUID.randomUUID().toString();
  }

  public String getRandomOwner() {
    return UUID.randomUUID().toString();
  }

  public String getRandomApiKey() {
    return UUID.randomUUID().toString();
  }
}
