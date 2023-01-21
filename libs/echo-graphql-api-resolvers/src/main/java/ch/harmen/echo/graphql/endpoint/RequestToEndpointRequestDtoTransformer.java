package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.graphql.request.RequestHeaderDto;
import ch.harmen.echo.request.Request;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

@Component
final class RequestToEndpointRequestDtoTransformer
  implements Function<Request, EndpointRequestDto> {

  @Override
  public EndpointRequestDto apply(final Request request) {
    return new EndpointRequestDto(
      request.id(),
      request.receiveTime().toString(),
      request.uri().toString(),
      request.method().toString(),
      request
        .headers()
        .entrySet()
        .stream()
        .map(this::toRequestHeaderDto)
        .collect(Collectors.toList()),
      request.body().map(Base64Utils::encodeToString)
    );
  }

  private RequestHeaderDto toRequestHeaderDto(
    final Entry<String, List<String>> header
  ) {
    return new RequestHeaderDto(header.getKey(), header.getValue());
  }
}
