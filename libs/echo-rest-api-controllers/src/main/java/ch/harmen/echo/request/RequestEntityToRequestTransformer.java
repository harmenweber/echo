package ch.harmen.echo.request;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

@Component
class RequestEntityToRequestTransformer
  implements BiFunction<String, RequestEntity<byte[]>, Request> {

  @Override
  public Request apply(
    final String endpointId,
    RequestEntity<byte[]> requestEntity
  ) {
    return new Request(
      UUID.randomUUID().toString(),
      endpointId,
      Instant.now(),
      requestEntity.getUrl(),
      requestEntity.getMethod(),
      requestEntity.getHeaders(),
      Optional.ofNullable(requestEntity.getBody())
    );
  }
}
