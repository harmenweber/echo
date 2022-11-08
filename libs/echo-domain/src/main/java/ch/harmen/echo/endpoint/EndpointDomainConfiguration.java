package ch.harmen.echo.endpoint;

import ch.harmen.echo.user.UserDomainConfiguration;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointDomainConfiguration {

  private final UserDomainConfiguration userDomainConfiguration;

  public EndpointDomainConfiguration(
    UserDomainConfiguration userDomainConfiguration
  ) {
    this.userDomainConfiguration =
      Objects.requireNonNull(userDomainConfiguration);
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
      this.userDomainConfiguration.currentUserContextSupplier()
    );
  }
}
