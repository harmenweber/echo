package ch.harmen.echo.graphql.endpoint;

import java.util.Objects;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class EndpointDtoToEndpointEdgeDtoTransformer
  implements Function<EndpointDto, EndPointEdgeDto> {

  private final EndpointDtoCursorFactory endpointDtoCursorFactory;

  public EndpointDtoToEndpointEdgeDtoTransformer(
    EndpointDtoCursorFactory endpointDtoCursorFactory
  ) {
    this.endpointDtoCursorFactory =
      Objects.requireNonNull(endpointDtoCursorFactory);
  }

  @Override
  public EndPointEdgeDto apply(EndpointDto endpointDto) {
    return new EndPointEdgeDto(
      this.endpointDtoCursorFactory.apply(endpointDto),
      endpointDto
    );
  }
}
