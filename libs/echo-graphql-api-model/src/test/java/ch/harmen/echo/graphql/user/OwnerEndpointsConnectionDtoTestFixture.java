package ch.harmen.echo.graphql.user;

import ch.harmen.echo.graphql.common.PageInfoDto;
import ch.harmen.echo.graphql.common.PageInfoDtoTestFixture;
import ch.harmen.echo.graphql.endpoint.EndPointEdgeDto;
import ch.harmen.echo.graphql.endpoint.EndPointEdgeDtoTestFixture;
import com.github.javafaker.Faker;
import java.util.List;
import java.util.stream.Stream;

public final class OwnerEndpointsConnectionDtoTestFixture {

  public static final int RANDOM_EDGES_MIN_COUNT = 1;
  public static final int RANDOM_EDGES_MAX_COUNT = 3;

  private final Faker faker = new Faker();
  private final EndPointEdgeDtoTestFixture endPointEdgeDtoTestFixture = new EndPointEdgeDtoTestFixture();
  private final PageInfoDtoTestFixture pageInfoDtoTestFixture = new PageInfoDtoTestFixture();

  public OwnerEndpointsConnectionDto create() {
    return new OwnerEndpointsConnectionDto(
      getRandomEdges(),
      getRandomPageInfo()
    );
  }

  private List<EndPointEdgeDto> getRandomEdges() {
    return Stream
      .generate(this.endPointEdgeDtoTestFixture::create)
      .limit(
        this.faker.random()
          .nextInt(RANDOM_EDGES_MIN_COUNT, RANDOM_EDGES_MAX_COUNT)
      )
      .toList();
  }

  public PageInfoDto getRandomPageInfo() {
    return this.pageInfoDtoTestFixture.create();
  }
}
