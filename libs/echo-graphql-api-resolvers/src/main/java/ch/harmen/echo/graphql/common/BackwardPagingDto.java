package ch.harmen.echo.graphql.common;

import java.util.Optional;

public record BackwardPagingDto(
  Optional<String> before,
  Optional<String> after,
  int last
) {}
