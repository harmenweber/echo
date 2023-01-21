package ch.harmen.echo.graphql.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.harmen.echo.graphql.common.StringToCursorConverter;
import ch.harmen.echo.request.ReceiveTimeAndId;
import ch.harmen.echo.request.ReceiveTimeAndIdTestFixture;
import org.junit.jupiter.api.Test;

final class ReceiveTimeAndIdToCursorConverterTest {

  private final ReceiveTimeAndIdToCursorConverter converter = new ReceiveTimeAndIdToCursorConverter(
    new StringToCursorConverter()
  );
  private final ReceiveTimeAndIdTestFixture testFixture = new ReceiveTimeAndIdTestFixture();

  @Test
  void toCursor_andBack() {
    final ReceiveTimeAndId expectedResult = this.testFixture.create();
    final String cursor =
      this.converter.receiveTimeAndIdToCursor(expectedResult);
    final ReceiveTimeAndId actualResult =
      this.converter.cursorToReceiveTimeAndId(cursor);
    assertThat(actualResult).isEqualTo(expectedResult);
  }
}
