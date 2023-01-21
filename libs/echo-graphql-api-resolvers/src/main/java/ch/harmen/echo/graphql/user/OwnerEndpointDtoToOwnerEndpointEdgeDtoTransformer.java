package ch.harmen.echo.graphql.user;

import ch.harmen.echo.graphql.common.StringToCursorConverter;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
class OwnerEndpointDtoToOwnerEndpointEdgeDtoTransformer
  implements Function<OwnerEndpointDto, OwnerEndPointEdgeDto> {

  private final StringToCursorConverter stringToCursorConverter;

  OwnerEndpointDtoToOwnerEndpointEdgeDtoTransformer(
    final StringToCursorConverter stringToCursorConverter
  ) {
    this.stringToCursorConverter =
      Objects.requireNonNull(stringToCursorConverter);
  }

  @Override
  public OwnerEndPointEdgeDto apply(final OwnerEndpointDto ownerEndpointDto) {
    return new OwnerEndPointEdgeDto(
      this.stringToCursorConverter.stringToCursor(ownerEndpointDto.id()),
      ownerEndpointDto
    );
  }
}
