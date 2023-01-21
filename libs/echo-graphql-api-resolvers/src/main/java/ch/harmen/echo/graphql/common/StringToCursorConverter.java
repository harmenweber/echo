package ch.harmen.echo.graphql.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Component
public class StringToCursorConverter {

  static final Charset CHARSET = StandardCharsets.UTF_8;

  public String stringToCursor(final String id) {
    return Base64Utils.encodeToString(id.getBytes(CHARSET));
  }

  public String cursorToString(final String cursor) {
    return new String(Base64Utils.decodeFromString(cursor), CHARSET);
  }
}
