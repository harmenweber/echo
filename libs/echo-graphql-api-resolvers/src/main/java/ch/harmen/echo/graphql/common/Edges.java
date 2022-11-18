package ch.harmen.echo.graphql.common;

import graphql.com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;

public final class Edges {

  public static <EDGE> List<EDGE> takeFirst(
    final List<EDGE> edges,
    final int numberOfFirstEdgesToTake
  ) {
    if (edges.size() > numberOfFirstEdgesToTake) {
      return edges.stream().limit(numberOfFirstEdgesToTake).toList();
    } else {
      return edges;
    }
  }

  public static <EDGE> List<EDGE> takeLast(
    final List<EDGE> edges,
    final int numberOfLastEdgesToTake
  ) {
    if (edges.size() > numberOfLastEdgesToTake) {
      return Lists.reverse(
        Lists.reverse(edges).stream().limit(numberOfLastEdgesToTake).toList()
      );
    } else {
      return edges;
    }
  }

  public static <EDGE extends Edge<?>> Optional<EDGE> getFirst(
    final List<EDGE> edges
  ) {
    if (!edges.isEmpty()) {
      return Optional.of(edges.get(0));
    } else {
      return Optional.empty();
    }
  }

  public static <EDGE extends Edge<?>> Optional<EDGE> getLast(
    final List<EDGE> edges
  ) {
    if (!edges.isEmpty()) {
      return Optional.of(edges.get(edges.size() - 1));
    } else {
      return Optional.empty();
    }
  }
}
