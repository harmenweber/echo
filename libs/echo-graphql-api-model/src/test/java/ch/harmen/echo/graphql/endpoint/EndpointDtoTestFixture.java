package ch.harmen.echo.graphql.endpoint;

import java.util.UUID;

public final class EndpointDtoTestFixture {

  public EndpointDto create() {
    return new EndpointDto(getRandomId(), getRandomApiKey());
  }

  public String getRandomId() {
    return UUID.randomUUID().toString();
  }

  public String getRandomApiKey() {
    return UUID.randomUUID().toString();
  }
}
