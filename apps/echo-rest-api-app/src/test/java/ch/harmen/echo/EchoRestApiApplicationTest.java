package ch.harmen.echo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class EchoRestApiApplicationTest {

  @Autowired
  private ApplicationContext applicationContext;

  private WebTestClient webTestClient;

  @BeforeEach
  void setupWebTestClient() {
    this.webTestClient =
      WebTestClient.bindToApplicationContext(this.applicationContext).build();
  }

  @Test
  void appUpAndRunning() {
    this.webTestClient.get().uri("/").exchange().expectStatus().isNotFound();
  }
}
