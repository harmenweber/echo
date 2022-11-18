package ch.harmen.echo.graphql.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class UserDtoTest {

  private final UserDtoTestFixture testFixture = new UserDtoTestFixture();

  @Test
  void copyConstructor() {
    var original = this.testFixture.create();
    var copy = new UserDto(original);

    assertThat(copy).isEqualTo(original);
  }
}
