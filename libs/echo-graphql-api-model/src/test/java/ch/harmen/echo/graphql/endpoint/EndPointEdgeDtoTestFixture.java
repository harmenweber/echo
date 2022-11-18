package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.graphql.common.PageInfoDtoTestFixture;

public final class EndPointEdgeDtoTestFixture {

  private final PageInfoDtoTestFixture pageInfoDtoTestFixture = new PageInfoDtoTestFixture();
  private final EndpointDtoTestFixture endpointDtoTestFixture = new EndpointDtoTestFixture();

  public EndPointEdgeDto create() {
    return new EndPointEdgeDto(getRandomCursor(), getRandomNode());
  }

  private String getRandomCursor() {
    return this.pageInfoDtoTestFixture.getRandomCursor();
  }

  private EndpointDto getRandomNode() {
    return this.endpointDtoTestFixture.create();
  }
}
