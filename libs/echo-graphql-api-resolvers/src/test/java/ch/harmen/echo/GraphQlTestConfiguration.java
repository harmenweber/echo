package ch.harmen.echo;

import ch.harmen.echo.endpoint.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootApplication
public class GraphQlTestConfiguration {

  @Autowired
  private ApplicationContext applicationContext;

  @Bean
  HttpGraphQlTester graphQlTester() {
    return HttpGraphQlTester.create(
      WebTestClient
        .bindToApplicationContext(this.applicationContext)
        .configureClient()
        .baseUrl("/graphql")
        .build()
    );
  }

  @Bean
  SampleDataLoader sampleDataLoader(EndpointService endpointService) {
    return new SampleDataLoader(endpointService);
  }
}
