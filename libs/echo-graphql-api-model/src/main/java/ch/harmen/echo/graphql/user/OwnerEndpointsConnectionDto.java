package ch.harmen.echo.graphql.user;

import ch.harmen.echo.graphql.common.PageInfoDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record OwnerEndpointsConnectionDto(
  List<OwnerEndPointEdgeDto> edges,
  PageInfoDto pageInfo
) {
  public OwnerEndpointsConnectionDto(
    final List<OwnerEndPointEdgeDto> edges,
    final PageInfoDto pageInfo
  ) {
    this.edges = new ArrayList<>(Objects.requireNonNull(edges));
    this.pageInfo = Objects.requireNonNull(pageInfo);
  }

  public OwnerEndpointsConnectionDto(
    final OwnerEndpointsConnectionDto original
  ) {
    this(original.edges, original.pageInfo);
  }
}
