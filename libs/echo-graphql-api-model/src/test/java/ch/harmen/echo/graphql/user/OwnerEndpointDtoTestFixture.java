package ch.harmen.echo.graphql.user;

import java.util.UUID;

public final class OwnerEndpointDtoTestFixture {

  public OwnerEndpointDto create() {
    return new OwnerEndpointDto(getRandomId(), getRandomApiKey());
  }

  public String getRandomId() {
    return UUID.randomUUID().toString();
  }

  public String getRandomApiKey() {
    return UUID.randomUUID().toString();
  }
}
