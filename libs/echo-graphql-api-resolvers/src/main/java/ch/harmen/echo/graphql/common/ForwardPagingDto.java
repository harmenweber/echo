package ch.harmen.echo.graphql.common;

import java.util.Objects;
import java.util.Optional;

public record ForwardPagingDto(
  Optional<String> before,
  Optional<String> after,
  int first
) {
  public ForwardPagingDto {
    Objects.requireNonNull(before);
    Objects.requireNonNull(after);
  }
}
