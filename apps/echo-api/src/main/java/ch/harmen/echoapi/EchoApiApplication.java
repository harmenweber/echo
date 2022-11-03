package ch.harmen.echoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ch.harmen")
public class EchoApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(EchoApiApplication.class, args);
  }
}
