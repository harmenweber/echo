package ch.harmen.echo.rest.endpoint;

import ch.harmen.echo.endpoint.Endpoint;
import ch.harmen.echo.rest.endpoint.EndpointDto;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component("rest.endpointToEndpointDtoTransformer")
class EndpointToEndpointDtoTransformer
  implements Function<Endpoint, EndpointDto> {

  @Override
  public EndpointDto apply(Endpoint endpoint) {
    return new EndpointDto(endpoint.id(), endpoint.owner(), endpoint.apiKey());
  }
}
