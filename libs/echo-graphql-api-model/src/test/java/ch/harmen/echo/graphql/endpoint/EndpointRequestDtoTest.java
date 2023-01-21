package ch.harmen.echo.graphql.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class EndpointRequestDtoTest {

  private final EndpointRequestDtoTestFixture testFixture = new EndpointRequestDtoTestFixture();

  @Test
  void copyConstructor() {
    final EndpointRequestDto original = this.testFixture.create();
    final EndpointRequestDto copy = new EndpointRequestDto(original);
    assertThat(copy).isEqualTo(original);
  }
}
