package ch.harmen.echographqlapiapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ch.harmen")
public class EchoGraphqlApiAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(EchoGraphqlApiAppApplication.class, args);
  }
}
