package ch.harmen.echo.graphql.endpoint;

import java.util.UUID;

public final class EndpointRequestEdgeDtoTestFixture {

  private final EndpointRequestDtoTestFixture endpointRequestDtoTestFixture = new EndpointRequestDtoTestFixture();

  public EndpointRequestEdgeDto create() {
    return new EndpointRequestEdgeDto(getRandomCursor(), getRandomRequest());
  }

  public String getRandomCursor() {
    return UUID.randomUUID().toString();
  }

  public EndpointRequestDto getRandomRequest() {
    return this.endpointRequestDtoTestFixture.create();
  }
}
