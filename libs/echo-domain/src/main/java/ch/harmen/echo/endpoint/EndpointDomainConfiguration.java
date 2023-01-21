package ch.harmen.echo.endpoint;

import ch.harmen.echo.request.RequestDomainConfiguration;
import ch.harmen.echo.user.UserDomainConfiguration;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointDomainConfiguration {

  private final UserDomainConfiguration userDomainConfiguration;
  private final RequestDomainConfiguration requestDomainConfiguration;

  public EndpointDomainConfiguration(
    final UserDomainConfiguration userDomainConfiguration,
    final RequestDomainConfiguration requestDomainConfiguration
  ) {
    this.userDomainConfiguration =
      Objects.requireNonNull(userDomainConfiguration);
    this.requestDomainConfiguration =
      Objects.requireNonNull(requestDomainConfiguration);
  }

  @Bean
  public EndpointService endpointService() {
    return new EndpointService(
      new EndpointFactory(
        new EndpointIdFactory(),
        this.userDomainConfiguration.currentUserContextSupplier(),
        new EndpointApiKeyFactory()
      ),
      new EndpointRepository(),
      this.userDomainConfiguration.currentUserContextSupplier(),
      this.requestDomainConfiguration.requestService()
    );
  }
}
