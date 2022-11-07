package ch.harmen.echo.endpoint;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class EndpointIdFactory {

  String create() {
    return UUID.randomUUID().toString();
  }
}