package ch.harmen.echo.graphql.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class RequestHeaderDtoTest {

  private final RequestHeaderDtoTestFixture testFixture = new RequestHeaderDtoTestFixture();

  @Test
  void copyConstructor() {
    final RequestHeaderDto original = this.testFixture.create();
    final RequestHeaderDto copy = new RequestHeaderDto(original);
    assertThat(copy).isEqualTo(original);
  }
}
