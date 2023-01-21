package ch.harmen.echo.graphql.user;

import ch.harmen.echo.graphql.common.Edge;
import java.util.Objects;

public record OwnerEndPointEdgeDto(String cursor, OwnerEndpointDto node)
  implements Edge<OwnerEndpointDto> {
  public OwnerEndPointEdgeDto {
    Objects.requireNonNull(cursor);
    Objects.requireNonNull(node);
  }

  public OwnerEndPointEdgeDto(final OwnerEndPointEdgeDto original) {
    this(original.cursor, original.node);
  }
}
