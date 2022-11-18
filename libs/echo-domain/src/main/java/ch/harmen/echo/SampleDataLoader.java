package ch.harmen.echo;

import ch.harmen.echo.endpoint.Endpoint;
import ch.harmen.echo.endpoint.EndpointConstants;
import ch.harmen.echo.endpoint.EndpointService;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import reactor.core.publisher.Flux;

public class SampleDataLoader
  implements ApplicationListener<ApplicationReadyEvent> {

  private final EndpointService endpointService;

  public SampleDataLoader(EndpointService endpointService) {
    this.endpointService = Objects.requireNonNull(endpointService);
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    System.out.println("Creating sample data…");
    List<Endpoint> endpoints = Flux
      .fromStream(Stream.generate(this.endpointService::create))
      .take(EndpointConstants.MAX_ENDPOINTS_PER_OWNER)
      .flatMap(Function.identity())
      .collectList()
      .block();
    System.out.printf("Sample data created: %s%n", endpoints);
  }
}
