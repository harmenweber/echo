package ch.harmen.echo.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

final class CurrentUserContextSupplierTest {

  private final CurrentUserContextSupplier userContextProvider = new CurrentUserContextSupplier();

  @Test
  void getCurrent_returnsAnonymousUserContext() {
    assertThat(this.userContextProvider.get())
      .isEqualTo(CurrentUserContextSupplier.ANONYMOUS_USER_CONTEXT);
  }
}
