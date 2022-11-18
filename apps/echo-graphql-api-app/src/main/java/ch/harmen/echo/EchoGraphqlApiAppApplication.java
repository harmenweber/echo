package ch.harmen.echo;

import ch.harmen.echo.endpoint.EndpointService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EchoGraphqlApiAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(EchoGraphqlApiAppApplication.class, args);
  }

  /*
   * The GraphQL API currently does not allow to create new endpoints.
   * Therefore, we populate some sample data on startup of the GraphQL API application.
   */
  @Bean
  SampleDataLoader sampleDataLoader(EndpointService endpointService) {
    return new SampleDataLoader(endpointService);
  }
}
