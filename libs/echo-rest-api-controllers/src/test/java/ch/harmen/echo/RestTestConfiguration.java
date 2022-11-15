package ch.harmen.echo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootApplication
public class RestTestConfiguration {

  @Autowired
  private ApplicationContext applicationContext;

  @Bean
  WebTestClient webTestClient() {
    return WebTestClient
      .bindToApplicationContext(this.applicationContext)
      .build();
  }
}
