package ch.harmen.echo.rest.endpoint;

import ch.harmen.echo.rest.endpoint.EndpointDto;
import java.util.UUID;

public final class EndpointDtoTestFixture {

  public EndpointDto create() {
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
