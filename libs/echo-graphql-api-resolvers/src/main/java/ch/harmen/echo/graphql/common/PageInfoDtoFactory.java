package ch.harmen.echo.graphql.common;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PageInfoDtoFactory {

  /**
   * Calculate the page info from the fetched edges and the paging instructions.
   * <p/>
   * <strong>NOTE:</strong> The method assumes that more edges got fetched than requested.
   * Otherwise, the method cannot decide whether there is a previous or next page available. See
   * section <a
   * href="https://relay.dev/graphql/connections.htm#sec-undefined.PageInfo.Fields">PageInfo
   * Fields</a> for an explanation on how to calculate the page info fields.
   *
   * @param edges  The fetched edges. The method expects that more edges got fetched than
   *               requested.
   * @param paging The paging instructions from the GraphQL API client.
   * @param <EDGE> The edge type.
   * @return The page info.
   */
  public <EDGE extends Edge<?>> PageInfoDto create(
    final List<EDGE> edges,
    final PagingDto paging
  ) {
    final List<EDGE> limitedEdges = limitEdges(edges, paging);
    return new PageInfoDto(
      getFirst(limitedEdges).map(Edge::cursor),
      getLast(limitedEdges).map(Edge::cursor),
      calculateHasPreviousPage(edges, paging),
      calculateHasNextPage(edges, paging)
    );
  }

  private <EDGE extends Edge<?>> Optional<EDGE> getFirst(
    final List<EDGE> edges
  ) {
    if (!edges.isEmpty()) {
      return Optional.of(edges.get(0));
    } else {
      return Optional.empty();
    }
  }

  private <EDGE extends Edge<?>> Optional<EDGE> getLast(
    final List<EDGE> edges
  ) {
    if (!edges.isEmpty()) {
      return Optional.of(edges.get(edges.size() - 1));
    } else {
      return Optional.empty();
    }
  }

  private <EDGE> List<EDGE> limitEdges(
    final List<EDGE> edges,
    final PagingDto paging
  ) {
    if (paging.first().isPresent()) {
      return edges.stream().limit(paging.first().get()).toList();
    } else if (paging.last().isPresent()) {
      return edges.stream().limit(paging.last().get()).toList();
    } else {
      return edges;
    }
  }

  private <EDGE> boolean calculateHasPreviousPage(
    List<EDGE> edges,
    PagingDto paging
  ) {
    if (paging.last().isPresent()) {
      return edges.size() > paging.last().get();
    } else if (paging.after().isPresent()) {
      /* We cannot efficiently determine that elements exist prior to paging.after().
      See https://relay.dev/graphql/connections.htm#sec-undefined.PageInfo.Fields */
      return false;
    } else {
      return false;
    }
  }

  private <EDGE> boolean calculateHasNextPage(
    List<EDGE> edges,
    PagingDto paging
  ) {
    if (paging.first().isPresent()) {
      return edges.size() > paging.first().get();
    } else if (paging.before().isPresent()) {
      /* We cannot efficiently determine that elements exist following to paging.before().
      See https://relay.dev/graphql/connections.htm#sec-undefined.PageInfo.Fields */
      return false;
    } else {
      return false;
    }
  }
}
