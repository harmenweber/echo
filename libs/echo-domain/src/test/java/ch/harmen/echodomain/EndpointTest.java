package ch.harmen.echodomain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class EndpointTest {

  private final EndpointTestFixture testFixture = new EndpointTestFixture();

  @Test
  void equals() {
    final var endpoint1 = this.testFixture.create();
    final var endpoint2 = new Endpoint(endpoint1.id(), endpoint1.apiKey());
    assertThat(endpoint2).isEqualTo(endpoint1);
  }

}
