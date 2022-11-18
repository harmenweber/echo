package ch.harmen.echo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class EchoGraphqlApiAppApplicationTest {

  @Autowired
  private ApplicationContext applicationContext;

  private HttpGraphQlTester graphQlTester;

  @BeforeEach
  void setupWebTestClient() {
    this.graphQlTester =
      HttpGraphQlTester.create(
        WebTestClient
          .bindToApplicationContext(this.applicationContext)
          .configureClient()
          .baseUrl("/graphql")
          .build()
      );
  }

  @Test
  void appUpAndRunning() {
    var document = """
        {
        owner {
          id
        }
      }""";

    this.graphQlTester.document(document).executeAndVerify();
  }
}
