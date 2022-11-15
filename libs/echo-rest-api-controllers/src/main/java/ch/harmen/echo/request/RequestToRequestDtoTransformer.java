package ch.harmen.echo.request;

import java.util.function.Function;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Component
class RequestToRequestDtoTransformer implements Function<Request, RequestDto> {

  @Override
  public RequestDto apply(Request request) {
    return new RequestDto(
      request.id(),
      request.endpointId(),
      request.receiveTime(),
      request.uri(),
      request.method().name(),
      request.headers(),
      request.body().map(Base64Utils::encodeToString)
    );
  }
}
