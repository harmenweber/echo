package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.graphql.common.PageInfoDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record EndpointRequestsConnectionDto(
  List<EndpointRequestEdgeDto> edges,
  PageInfoDto pageInfo
) {
  public EndpointRequestsConnectionDto(
    final List<EndpointRequestEdgeDto> edges,
    final PageInfoDto pageInfo
  ) {
    this.edges = new ArrayList<>(Objects.requireNonNull(edges));
    this.pageInfo = Objects.requireNonNull(pageInfo);
  }

  public EndpointRequestsConnectionDto(
    final EndpointRequestsConnectionDto original
  ) {
    this(original.edges, original.pageInfo);
  }
}
