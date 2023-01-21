package ch.harmen.echo.graphql.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class EndpointRequestEdgeDtoTest {

  private final EndpointRequestEdgeDtoTestFixture testFixture = new EndpointRequestEdgeDtoTestFixture();

  @Test
  void copyConstructor() {
    final EndpointRequestEdgeDto original = this.testFixture.create();
    final EndpointRequestEdgeDto copy = new EndpointRequestEdgeDto(original);
    assertThat(copy).isEqualTo(original);
  }
}
