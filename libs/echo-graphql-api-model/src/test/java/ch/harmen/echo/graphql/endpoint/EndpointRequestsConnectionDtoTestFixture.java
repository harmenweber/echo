package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.graphql.common.PageInfoDto;
import ch.harmen.echo.graphql.common.PageInfoDtoTestFixture;
import com.github.javafaker.Faker;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EndpointRequestsConnectionDtoTestFixture {

  public static final int RANDOM_EDGES_MAX_SIZE = 10;

  private final EndpointRequestEdgeDtoTestFixture endpointRequestEdgeDtoTestFixture = new EndpointRequestEdgeDtoTestFixture();
  private final PageInfoDtoTestFixture pageInfoDtoTestFixture = new PageInfoDtoTestFixture();
  private final Faker faker = new Faker();

  public EndpointRequestsConnectionDto create() {
    return new EndpointRequestsConnectionDto(
      getRandomEdges(),
      getRandomPageInfo()
    );
  }

  public List<EndpointRequestEdgeDto> getRandomEdges() {
    return Stream
      .generate(this.endpointRequestEdgeDtoTestFixture::create)
      .limit(faker.random().nextInt(RANDOM_EDGES_MAX_SIZE))
      .collect(Collectors.toList());
  }

  public PageInfoDto getRandomPageInfo() {
    return this.pageInfoDtoTestFixture.create();
  }
}
