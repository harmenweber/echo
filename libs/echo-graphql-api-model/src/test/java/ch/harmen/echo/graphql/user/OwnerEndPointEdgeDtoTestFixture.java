package ch.harmen.echo.graphql.user;

import ch.harmen.echo.graphql.common.PageInfoDtoTestFixture;

public final class OwnerEndPointEdgeDtoTestFixture {

  private final PageInfoDtoTestFixture pageInfoDtoTestFixture = new PageInfoDtoTestFixture();
  private final OwnerEndpointDtoTestFixture ownerEndpointDtoTestFixture = new OwnerEndpointDtoTestFixture();

  public OwnerEndPointEdgeDto create() {
    return new OwnerEndPointEdgeDto(getRandomCursor(), getRandomNode());
  }

  private String getRandomCursor() {
    return this.pageInfoDtoTestFixture.getRandomCursor();
  }

  private OwnerEndpointDto getRandomNode() {
    return this.ownerEndpointDtoTestFixture.create();
  }
}
