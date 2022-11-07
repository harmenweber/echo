package ch.harmen.echo.endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class EndpointRepository {

  private final Map<String, Map<String, Endpoint>> endpointsByOwner = new HashMap<>();

  void save(final Endpoint endpoint) {
    this.endpointsByOwner.compute(
        endpoint.owner(),
        (key, value) -> {
          final Map<String, Endpoint> endpoints = Optional
            .ofNullable(value)
            .orElseGet(HashMap::new);
          endpoints.put(endpoint.id(), endpoint);
          return endpoints;
        }
      );
  }

  void delete(final Endpoint endpoint) {
    Optional
      .ofNullable(this.endpointsByOwner.get(endpoint.owner()))
      .ifPresent(endpoints -> endpoints.remove(endpoint.id()));
  }

  Optional<Endpoint> findByOwnerAndId(final String owner, final String id) {
    return Optional
      .ofNullable(this.endpointsByOwner.get(owner))
      .flatMap(endpoints -> Optional.ofNullable(endpoints.get(id)));
  }
}
