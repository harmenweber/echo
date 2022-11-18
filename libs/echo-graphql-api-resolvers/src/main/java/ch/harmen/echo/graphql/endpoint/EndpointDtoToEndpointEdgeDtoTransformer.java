package ch.harmen.echo.graphql.endpoint;

import java.util.Objects;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class EndpointDtoToEndpointEdgeDtoTransformer
  implements Function<EndpointDto, EndPointEdgeDto> {

  private final EndpointCursorConverter endpointCursorConverter;

  public EndpointDtoToEndpointEdgeDtoTransformer(
    EndpointCursorConverter endpointCursorConverter
  ) {
    this.endpointCursorConverter =
      Objects.requireNonNull(endpointCursorConverter);
  }

  @Override
  public EndPointEdgeDto apply(EndpointDto endpointDto) {
    return new EndPointEdgeDto(
      this.endpointCursorConverter.endpointDtoToCursor(endpointDto),
      endpointDto
    );
  }
}
