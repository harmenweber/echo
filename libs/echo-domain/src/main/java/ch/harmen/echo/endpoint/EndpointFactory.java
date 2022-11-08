package ch.harmen.echo.endpoint;

import ch.harmen.echo.user.CurrentUserContextSupplier;
import java.util.Objects;

class EndpointFactory {

  private final EndpointIdFactory endpointIdFactory;
  private final CurrentUserContextSupplier userContextProvider;
  private final EndpointApiKeyFactory endpointApiKeyFactory;

  EndpointFactory(
    final EndpointIdFactory endpointIdFactory,
    final CurrentUserContextSupplier userContextProvider,
    final EndpointApiKeyFactory endpointApiKeyFactory
  ) {
    this.endpointIdFactory = Objects.requireNonNull(endpointIdFactory);
    this.userContextProvider = Objects.requireNonNull(userContextProvider);
    this.endpointApiKeyFactory = Objects.requireNonNull(endpointApiKeyFactory);
  }

  Endpoint create() {
    return new Endpoint(
      this.endpointIdFactory.create(),
      this.userContextProvider.get().id(),
      this.endpointApiKeyFactory.create()
    );
  }
}
