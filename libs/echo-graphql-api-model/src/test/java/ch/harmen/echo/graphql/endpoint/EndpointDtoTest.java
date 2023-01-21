package ch.harmen.echo.graphql.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class EndpointDtoTest {

  private final EndpointDtoTestFixture testFixture = new EndpointDtoTestFixture();

  @Test
  void copyConstructor() {
    final EndpointDto original = this.testFixture.create();
    final EndpointDto copy = new EndpointDto(original);
    assertThat(copy).isEqualTo(original);
  }
}
