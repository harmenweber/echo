package ch.harmen.echo.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class ReceiveTimeAndIdTest {

  private final ReceiveTimeAndIdTestFixture testFixture = new ReceiveTimeAndIdTestFixture();

  @Test
  void copyConstructor() {
    final ReceiveTimeAndId original = this.testFixture.create();
    final ReceiveTimeAndId copy = new ReceiveTimeAndId(original);
    assertThat(copy).isEqualTo(original);
  }
}
