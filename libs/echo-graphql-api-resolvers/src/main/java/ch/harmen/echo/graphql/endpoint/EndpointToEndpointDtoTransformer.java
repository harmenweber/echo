package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.endpoint.Endpoint;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component("graphql.endpointToEndpointDtoTransformer")
public class EndpointToEndpointDtoTransformer
  implements Function<Endpoint, EndpointDto> {

  @Override
  public EndpointDto apply(Endpoint endpoint) {
    return new EndpointDto(endpoint.id(), endpoint.apiKey());
  }
}
