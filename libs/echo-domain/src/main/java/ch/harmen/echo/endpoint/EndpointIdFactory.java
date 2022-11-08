package ch.harmen.echo.endpoint;

import java.util.UUID;

class EndpointIdFactory {

  String create() {
    return UUID.randomUUID().toString();
  }
}
