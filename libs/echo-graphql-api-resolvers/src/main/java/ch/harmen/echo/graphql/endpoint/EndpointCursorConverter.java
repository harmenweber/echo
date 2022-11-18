package ch.harmen.echo.graphql.endpoint;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Component
public class EndpointCursorConverter {

  static final Charset CHARSET = StandardCharsets.UTF_8;

  public String endpointDtoToCursor(EndpointDto endpointDto) {
    return endpointIdToCursor(endpointDto.id());
  }

  public String endpointIdToCursor(final String id) {
    return Base64Utils.encodeToString(id.getBytes(CHARSET));
  }

  public String cursorToEndpointId(final String cursor) {
    return new String(Base64Utils.decodeFromString(cursor), CHARSET);
  }
}
