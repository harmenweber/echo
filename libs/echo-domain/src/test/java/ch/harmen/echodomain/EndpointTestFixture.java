package ch.harmen.echodomain;

import java.util.UUID;

public final class EndpointTestFixture {

  Endpoint create() {
    return new Endpoint(getRandomId(), getRandomApiKey());
  }

  public String getRandomId() {
    return UUID.randomUUID().toString();
  }

  public String getRandomApiKey() {
    return UUID.randomUUID().toString();
  }
}
