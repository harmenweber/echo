package ch.harmen.echo.graphql.common;

public interface Edge<NODE> {
  String cursor();

  NODE node();
}
