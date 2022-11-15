package ch.harmen.echo.endpoint;

import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
class EndpointToEndpointDtoTransformer
  implements Function<Endpoint, EndpointDto> {

  @Override
  public EndpointDto apply(Endpoint endpoint) {
    return new EndpointDto(endpoint.id(), endpoint.owner(), endpoint.apiKey());
  }
}
