package ch.harmen.echo.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;

@Configuration
class EndpointRestTestConfiguration {

  @Autowired
  private WebTestClient webTestClient;

  @Bean
  EndpointRestClient endpointRestClient() {
    return new EndpointRestClient(webTestClient);
  }
}
