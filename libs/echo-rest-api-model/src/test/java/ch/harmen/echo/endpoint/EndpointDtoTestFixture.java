package ch.harmen.echo.endpoint;

import java.util.UUID;

public final class EndpointDtoTestFixture {

  EndpointDto create() {
    return new EndpointDto(getRandomId(), getRandomOwner(), getRandomApiKey());
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
