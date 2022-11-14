package ch.harmen.echo.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class RequestTest {

  private final RequestTestFixture testFixture = new RequestTestFixture();

  @Test
  void copyConstructor() {
    final Request original = this.testFixture.create();
    final Request copy = new Request(original);
    assertThat(copy).isEqualTo(original);
  }
}
