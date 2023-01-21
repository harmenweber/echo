package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.graphql.common.StringToCursorConverter;
import ch.harmen.echo.request.ReceiveTimeAndId;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
class ReceiveTimeAndIdToCursorConverter {

  private final StringToCursorConverter stringToCursorConverter;

  ReceiveTimeAndIdToCursorConverter(
    final StringToCursorConverter stringToCursorConverter
  ) {
    this.stringToCursorConverter =
      Objects.requireNonNull(stringToCursorConverter);
  }

  String receiveTimeAndIdToCursor(final ReceiveTimeAndId receiveTimeAndId) {
    return this.stringToCursorConverter.stringToCursor(
        receiveTimeAndIdToString(receiveTimeAndId)
      );
  }

  String receiveTimeAndIdToString(final ReceiveTimeAndId receiveTimeAndId) {
    return "%s|%s".formatted(
        receiveTimeAndId.receiveTime(),
        receiveTimeAndId.id()
      );
  }

  ReceiveTimeAndId cursorToReceiveTimeAndId(final String cursor) {
    return stringToReceiveTimeAndId(
      this.stringToCursorConverter.cursorToString(cursor)
    );
  }

  ReceiveTimeAndId stringToReceiveTimeAndId(final String string) {
    try {
      final String[] parts = string.split("\\|", 2);
      return new ReceiveTimeAndId(Instant.parse(parts[0]), parts[1]);
    } catch (Throwable throwable) {
      throw new IllegalArgumentException(
        "Invalid request cursor: %s.".formatted(string),
        throwable
      );
    }
  }
}
