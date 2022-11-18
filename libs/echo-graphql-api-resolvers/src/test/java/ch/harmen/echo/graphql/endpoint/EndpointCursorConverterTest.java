package ch.harmen.echo.graphql.endpoint;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

final class EndpointCursorConverterTest {

  private final EndpointCursorConverter converter = new EndpointCursorConverter();
  private final EndpointDtoTestFixture testFixture = new EndpointDtoTestFixture();

  @Test
  void toCursor_andBack() {
    final var expectedId = this.testFixture.getRandomId();
    final var cursor = this.converter.endpointIdToCursor(expectedId);
    final var actualId = this.converter.cursorToEndpointId(cursor);
    assertThat(actualId).isEqualTo(expectedId);
  }
}
