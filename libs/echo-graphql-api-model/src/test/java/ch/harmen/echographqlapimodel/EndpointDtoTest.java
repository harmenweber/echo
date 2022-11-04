package ch.harmen.echographqlapimodel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class EndpointDtoTest {

  private final EndpointDtoTestFixture testFixture = new EndpointDtoTestFixture();

  @Test
  void equals() {
    final var endpoint1 = this.testFixture.create();
    final var endpoint2 = new EndpointDto(endpoint1.id(), endpoint1.apiKey());
    assertThat(endpoint2).isEqualTo(endpoint1);
  }
}
