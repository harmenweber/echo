package ch.harmen.echo.graphql.endpoint;

import java.util.UUID;

public final class EndpointDtoTestFixture {

  private final EndpointRequestsConnectionDtoTestFixture endpointRequestsConnectionDtoTestFixture = new EndpointRequestsConnectionDtoTestFixture();

  public EndpointDto create() {
    return new EndpointDto(
      getRandomId(),
      getRandomApiKey(),
      getRandomRequests()
    );
  }

  public String getRandomId() {
    return UUID.randomUUID().toString();
  }

  public String getRandomApiKey() {
    return UUID.randomUUID().toString();
  }

  public EndpointRequestsConnectionDto getRandomRequests() {
    return this.endpointRequestsConnectionDtoTestFixture.create();
  }
}
