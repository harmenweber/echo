package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.request.ReceiveTimeAndId;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
class EndpointRequestDtoToEndpointRequestEdgeDtoTransformer
  implements Function<EndpointRequestDto, EndpointRequestEdgeDto> {

  private final ReceiveTimeAndIdToCursorConverter receiveTimeAndIdToCursorConverter;

  EndpointRequestDtoToEndpointRequestEdgeDtoTransformer(
    final ReceiveTimeAndIdToCursorConverter receiveTimeAndIdToCursorConverter
  ) {
    this.receiveTimeAndIdToCursorConverter =
      Objects.requireNonNull(receiveTimeAndIdToCursorConverter);
  }

  @Override
  public EndpointRequestEdgeDto apply(EndpointRequestDto request) {
    return new EndpointRequestEdgeDto(
      this.receiveTimeAndIdToCursorConverter.receiveTimeAndIdToCursor(
          new ReceiveTimeAndId(
            Instant.parse(request.receiveTime()),
            request.id()
          )
        ),
      request
    );
  }
}
