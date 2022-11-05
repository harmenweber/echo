package ch.harmen.echorestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ch.harmen")
public class EchoRestApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(EchoRestApiApplication.class, args);
  }
}
