package ch.harmen.echo.rest.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class RequestDtoTest {

  private final RequestDtoTestFixture testFixture = new RequestDtoTestFixture();

  @Test
  void copyConstructor() {
    var original = this.testFixture.create();
    var copy = new RequestDto(original);

    assertThat(copy).isEqualTo(original);
  }
}
