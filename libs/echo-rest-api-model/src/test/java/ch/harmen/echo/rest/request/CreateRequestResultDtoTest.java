package ch.harmen.echo.rest.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class CreateRequestResultDtoTest {

  private final CreateRequestResultDtoTestFixture testFixture = new CreateRequestResultDtoTestFixture();

  @Test
  void copyConstructor() {
    var original = this.testFixture.create();
    var copy = new CreateRequestResultDto(original);

    assertThat(copy).isEqualTo(original);
  }
}
