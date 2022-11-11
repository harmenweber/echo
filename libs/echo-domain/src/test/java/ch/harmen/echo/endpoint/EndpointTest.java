package ch.harmen.echo.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class EndpointTest {

  private final EndpointTestFixture testFixture = new EndpointTestFixture();

  @Test
  void copyConstructor() {
    final var original = this.testFixture.create();
    final var copy = new Endpoint(original);
    assertThat(copy).isEqualTo(original);
  }
}
