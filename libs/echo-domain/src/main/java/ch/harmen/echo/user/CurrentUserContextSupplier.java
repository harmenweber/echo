package ch.harmen.echo.user;

public class CurrentUserContextSupplier {

  static final UserContext ANONYMOUS_USER_CONTEXT = new UserContext(
    "anonymous"
  );

  public UserContext get() {
    // As long as we haven't set up authentication, we use an anonymous user context.
    return ANONYMOUS_USER_CONTEXT;
  }
}
