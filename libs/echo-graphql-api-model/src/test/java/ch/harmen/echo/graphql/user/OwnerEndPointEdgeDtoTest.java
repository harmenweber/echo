package ch.harmen.echo.graphql.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class OwnerEndPointEdgeDtoTest {

  private final OwnerEndPointEdgeDtoTestFixture testFixture = new OwnerEndPointEdgeDtoTestFixture();

  @Test
  void copyConstructor() {
    var original = this.testFixture.create();
    var copy = new OwnerEndPointEdgeDto(original);

    assertThat(copy).isEqualTo(original);
  }
}
