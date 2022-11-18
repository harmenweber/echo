package ch.harmen.echo.graphql.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class OwnerEndpointsConnectionDtoTest {

  private final OwnerEndpointsConnectionDtoTestFixture testFixture = new OwnerEndpointsConnectionDtoTestFixture();

  @Test
  void copyConstructor() {
    var original = this.testFixture.create();
    var copy = new OwnerEndpointsConnectionDto(original);

    assertThat(copy).isEqualTo(original);
  }
}
