package ch.harmen.echo.rest.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;

@Configuration
class RequestRestTestConfiguration {

  @Autowired
  private WebTestClient webTestClient;

  @Bean
  RequestRestClient requestRestClient() {
    return new RequestRestClient(this.webTestClient);
  }
}
