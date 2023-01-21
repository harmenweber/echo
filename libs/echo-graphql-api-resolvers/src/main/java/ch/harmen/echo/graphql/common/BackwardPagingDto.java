package ch.harmen.echo.graphql.common;

import java.util.Objects;
import java.util.Optional;

public record BackwardPagingDto(
  Optional<String> before,
  Optional<String> after,
  int last
) {
  public BackwardPagingDto {
    Objects.requireNonNull(before);
    Objects.requireNonNull(after);
  }
}
