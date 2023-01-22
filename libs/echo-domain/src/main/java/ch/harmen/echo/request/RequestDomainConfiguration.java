package ch.harmen.echo.request;

import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestDomainConfiguration {

  private final RequestSubscriptionConfiguration requestSubscriptionConfiguration;

  public RequestDomainConfiguration(
    final RequestSubscriptionConfiguration requestSubscriptionConfiguration
  ) {
    this.requestSubscriptionConfiguration =
      Objects.requireNonNull(requestSubscriptionConfiguration);
  }

  @Bean
  public RequestService requestService() {
    return new RequestService(
      new RequestRepository(),
      this.requestSubscriptionConfiguration.requestSink(),
      this.requestSubscriptionConfiguration.requestFlux()
    );
  }
}
