package ch.harmen.echo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class EchoApiAppApplicationTest {

  @Autowired
  private ApplicationContext applicationContext;

  private WebTestClient restApiTester;
  private HttpGraphQlTester graphQlApiTester;

  @BeforeEach
  void setupWebTestClient() {
    this.restApiTester =
      WebTestClient.bindToApplicationContext(this.applicationContext).build();
    this.graphQlApiTester =
      HttpGraphQlTester.create(
        WebTestClient
          .bindToApplicationContext(this.applicationContext)
          .configureClient()
          .baseUrl("/graphql")
          .build()
      );
  }

  @Test
  void restApi_isUpAndRunning() {
    this.restApiTester.get().uri("/").exchange().expectStatus().isNotFound();
  }

  @Test
  void graphQlApi_isUpAndRunning() {
    var document = """
        {
        owner {
          id
        }
      }""";

    this.graphQlApiTester.document(document).executeAndVerify();
  }
}
