package ch.harmen.echo.request;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestDomainConfiguration {

  @Bean
  public RequestService requestService() {
    return new RequestService(new RequestRepository());
  }
}
