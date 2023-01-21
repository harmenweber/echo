package ch.harmen.echo.graphql.common;

import java.util.Objects;
import java.util.Optional;

public record PageInfoDto(
  Optional<String> startCursor,
  Optional<String> endCursor,
  boolean hasPreviousPage,
  boolean hasNextPage
) {
  public PageInfoDto {
    Objects.requireNonNull(startCursor);
    Objects.requireNonNull(endCursor);
  }

  public PageInfoDto(final PageInfoDto original) {
    this(
      original.startCursor,
      original.endCursor,
      original.hasPreviousPage,
      original.hasNextPage
    );
  }
}
