package ch.harmen.echo.graphql.common;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PageInfoDtoFactory {

  public <EDGE extends Edge<?>> PageInfoDto createForForwardPaging(
    final List<EDGE> edges,
    final boolean hasMoreEdges
  ) {
    return new PageInfoDto(
      Edges.getFirst(edges).map(Edge::cursor),
      Edges.getLast(edges).map(Edge::cursor),
      false,
      hasMoreEdges
    );
  }

  public <EDGE extends Edge<?>> PageInfoDto createForBackwardPaging(
    final List<EDGE> edges,
    final boolean hasMoreEdges
  ) {
    return new PageInfoDto(
      Edges.getFirst(edges).map(Edge::cursor),
      Edges.getLast(edges).map(Edge::cursor),
      hasMoreEdges,
      false
    );
  }
}
