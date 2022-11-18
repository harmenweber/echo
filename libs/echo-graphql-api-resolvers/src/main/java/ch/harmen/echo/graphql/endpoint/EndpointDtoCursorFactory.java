package ch.harmen.echo.graphql.endpoint;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Component
public class EndpointDtoCursorFactory implements Function<EndpointDto, String> {

  @Override
  public String apply(EndpointDto endpointDto) {
    return Base64Utils.encodeToString(
      endpointDto.id().getBytes(StandardCharsets.UTF_8)
    );
  }
}
