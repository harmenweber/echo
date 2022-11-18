package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.graphql.common.Edge;

public record EndPointEdgeDto(String cursor, EndpointDto node)
  implements Edge<EndpointDto> {
  public EndPointEdgeDto(final EndPointEdgeDto original) {
    this(original.cursor, original.node);
  }
}
