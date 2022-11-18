package ch.harmen.echo.graphql.common;

import java.util.Optional;

public record ForwardPagingDto(
  Optional<String> before,
  Optional<String> after,
  int first
) {}
