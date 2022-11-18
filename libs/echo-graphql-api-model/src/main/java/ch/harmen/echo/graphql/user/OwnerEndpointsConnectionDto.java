package ch.harmen.echo.graphql.user;

import ch.harmen.echo.graphql.common.PageInfoDto;
import ch.harmen.echo.graphql.endpoint.EndPointEdgeDto;
import java.util.List;

public record OwnerEndpointsConnectionDto(
  List<EndPointEdgeDto> edges,
  PageInfoDto pageInfo
) {
  public OwnerEndpointsConnectionDto(
    final OwnerEndpointsConnectionDto original
  ) {
    this(original.edges, original.pageInfo);
  }
}
