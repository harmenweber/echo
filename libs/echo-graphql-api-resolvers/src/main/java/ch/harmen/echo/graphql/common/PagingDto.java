package ch.harmen.echo.graphql.common;

import java.util.Objects;
import java.util.Optional;

public record PagingDto(
  Optional<String> before,
  Optional<String> after,
  Optional<Integer> first,
  Optional<Integer> last
) {
  public PagingDto {
    Objects.requireNonNull(before);
    Objects.requireNonNull(after);
    Objects.requireNonNull(first);
    Objects.requireNonNull(last);
  }
}
