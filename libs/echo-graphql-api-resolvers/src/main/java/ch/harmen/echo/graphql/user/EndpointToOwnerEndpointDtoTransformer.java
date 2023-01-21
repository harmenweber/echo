package ch.harmen.echo.graphql.user;

import ch.harmen.echo.endpoint.Endpoint;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
class EndpointToOwnerEndpointDtoTransformer
  implements Function<Endpoint, OwnerEndpointDto> {

  @Override
  public OwnerEndpointDto apply(final Endpoint endpoint) {
    return new OwnerEndpointDto(endpoint.id(), endpoint.apiKey());
  }
}
