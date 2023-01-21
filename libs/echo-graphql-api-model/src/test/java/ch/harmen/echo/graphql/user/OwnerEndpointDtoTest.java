package ch.harmen.echo.graphql.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class OwnerEndpointDtoTest {

  private final OwnerEndpointDtoTestFixture testFixture = new OwnerEndpointDtoTestFixture();

  @Test
  void copyConstructor() {
    var original = this.testFixture.create();
    var copy = new OwnerEndpointDto(original);

    assertThat(copy).isEqualTo(original);
  }
}
