package ch.harmen.echo.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import ch.harmen.echo.user.CurrentUserContextSupplier;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

final class EndpointServiceTest {

  private final CurrentUserContextSupplier currentUserContextSupplier = new CurrentUserContextSupplier();
  private final EndpointService endpointService = new EndpointService(
    new EndpointFactory(
      new EndpointIdFactory(),
      this.currentUserContextSupplier,
      new EndpointApiKeyFactory()
    ),
    new EndpointRepository()
  );

  @Test
  void create_returnsEndpointWithOwnerEqualToCurrentUser() {
    final Endpoint endpoint = this.endpointService.create();
    assertAll(
      () -> assertThat(endpoint).isNotNull(),
      () ->
        assertThat(endpoint.owner())
          .isEqualTo(this.currentUserContextSupplier.get().id())
    );
  }

  @Test
  void create_savedNewlyCreatedEndpoint() {
    final Endpoint endpoint = this.endpointService.create();
    Optional<Endpoint> loadedEndpoint =
      this.endpointService.findByOwnerAndId(endpoint.owner(), endpoint.id());
    assertThat(loadedEndpoint).isEqualTo(Optional.of(endpoint));
  }

  @Test
  void create_returnsEndpointsWithUniqueIds() {
    final long limit = 100;
    Set<String> endpointIds = Stream
      .generate(this.endpointService::create)
      .map(Endpoint::id)
      .limit(limit)
      .collect(Collectors.toUnmodifiableSet());
    assertThat(endpointIds.size()).isEqualTo(limit);
  }

  @Test
  void create_returnsEndpointsWithUniqueApiKeys() {
    final long limit = 100;
    Set<String> endpointApiKeys = Stream
      .generate(this.endpointService::create)
      .map(Endpoint::apiKey)
      .limit(limit)
      .collect(Collectors.toUnmodifiableSet());
    assertThat(endpointApiKeys.size()).isEqualTo(limit);
  }

  @Test
  void delete() {
    final Endpoint endpoint = this.endpointService.create();

    Optional<Endpoint> endpointLoadedBeforeDelete =
      this.endpointService.findByOwnerAndId(endpoint.owner(), endpoint.id());

    this.endpointService.delete(endpoint);

    Optional<Endpoint> endpointLoadedAfterDelete =
      this.endpointService.findByOwnerAndId(endpoint.owner(), endpoint.id());

    assertAll(
      () -> assertThat(endpointLoadedBeforeDelete).isPresent(),
      () -> assertThat(endpointLoadedAfterDelete).isEmpty()
    );
  }
}
