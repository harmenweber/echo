package ch.harmen.echo.endpoint;

import java.util.UUID;

class EndpointApiKeyFactory {

  String create() {
    return UUID.randomUUID().toString();
  }
}
