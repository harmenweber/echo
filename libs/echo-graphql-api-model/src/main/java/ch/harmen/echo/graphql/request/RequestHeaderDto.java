package ch.harmen.echo.graphql.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record RequestHeaderDto(String name, List<String> values) {
  public RequestHeaderDto(final String name, final List<String> values) {
    this.name = Objects.requireNonNull(name);
    this.values = new ArrayList<>(Objects.requireNonNull(values));
  }

  public RequestHeaderDto(final RequestHeaderDto original) {
    this(original.name, original.values);
  }
}
