package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.graphql.common.Edge;
import java.util.Objects;

public record EndpointRequestEdgeDto(String cursor, EndpointRequestDto node)
  implements Edge<EndpointRequestDto> {
  public EndpointRequestEdgeDto {
    Objects.requireNonNull(cursor);
    Objects.requireNonNull(node);
  }

  public EndpointRequestEdgeDto(final EndpointRequestEdgeDto original) {
    this(original.cursor, original.node);
  }
}
