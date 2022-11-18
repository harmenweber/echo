package ch.harmen.echo.graphql.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class EndPointEdgeDtoTest {

  private final EndPointEdgeDtoTestFixture testFixture = new EndPointEdgeDtoTestFixture();

  @Test
  void copyConstructor() {
    var original = this.testFixture.create();
    var copy = new EndPointEdgeDto(original);

    assertThat(copy).isEqualTo(original);
  }
}
