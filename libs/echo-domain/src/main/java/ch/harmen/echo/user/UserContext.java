package ch.harmen.echo.user;

import java.util.Objects;

public record UserContext(String id) {
  public UserContext {
    Objects.requireNonNull(id);
  }
}
