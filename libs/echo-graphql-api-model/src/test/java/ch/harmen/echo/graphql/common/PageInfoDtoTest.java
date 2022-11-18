package ch.harmen.echo.graphql.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class PageInfoDtoTest {

  private final PageInfoDtoTestFixture testFixture = new PageInfoDtoTestFixture();

  @Test
  void copyConstructor() {
    var original = this.testFixture.create();
    var copy = new PageInfoDto(original);

    assertThat(copy).isEqualTo(original);
  }
}
