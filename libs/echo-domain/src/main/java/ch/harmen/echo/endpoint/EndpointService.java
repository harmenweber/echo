package ch.harmen.echo.endpoint;

import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class EndpointService {

  private final EndpointFactory endpointFactory;
  private final EndpointRepository endpointRepository;

  public EndpointService(
    EndpointFactory endpointFactory,
    EndpointRepository endpointRepository
  ) {
    this.endpointFactory = Objects.requireNonNull(endpointFactory);
    this.endpointRepository = Objects.requireNonNull(endpointRepository);
  }

  public Endpoint create() {
    final Endpoint endpoint = this.endpointFactory.create();
    this.endpointRepository.save(endpoint);
    return endpoint;
  }

  public void delete(final Endpoint endpoint) {
    Objects.requireNonNull(endpoint);
    this.endpointRepository.delete(endpoint);
  }

  public Optional<Endpoint> findByOwnerAndId(
    final String owner,
    final String id
  ) {
    Objects.requireNonNull(owner);
    Objects.requireNonNull(id);
    return this.endpointRepository.findByOwnerAndId(owner, id);
  }
}
