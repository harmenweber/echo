package ch.harmen.echo.graphql.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

final class StringToCursorConverterTest {

  private final StringToCursorConverter converter = new StringToCursorConverter();

  @Test
  void toCursor_andBack() {
    final var expectedId = getRandomId();
    final var cursor = this.converter.stringToCursor(expectedId);
    final var actualId = this.converter.cursorToString(cursor);
    assertThat(actualId).isEqualTo(expectedId);
  }

  private String getRandomId() {
    return UUID.randomUUID().toString();
  }
}
