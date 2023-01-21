package ch.harmen.echo.graphql.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class EndpointRequestsConnectionDtoTest {

  private final EndpointRequestsConnectionDtoTestFixture testFixture = new EndpointRequestsConnectionDtoTestFixture();

  @Test
  void copyConstructor() {
    final EndpointRequestsConnectionDto original = this.testFixture.create();
    final EndpointRequestsConnectionDto copy = new EndpointRequestsConnectionDto(
      original
    );
    assertThat(copy).isEqualTo(original);
  }
}
