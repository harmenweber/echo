package ch.harmen.echo.request;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.MimeType;

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
      encodeToBase64(
        request.body(),
        getCharset(request).orElse(StandardCharsets.UTF_8)
      )
    );
  }

  private String encodeToBase64(byte[] body, Charset charset) {
    return new String(Base64Utils.encode(body), charset);
  }

  private static Optional<Charset> getCharset(Request request) {
    return Optional
      .ofNullable(request.headers())
      .map(HttpHeaders::getContentType)
      .map(MimeType::getCharset);
  }
}
