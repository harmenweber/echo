package ch.harmen.echo.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDomainConfiguration {

  @Bean
  public CurrentUserContextSupplier currentUserContextSupplier() {
    return new CurrentUserContextSupplier();
  }
}
