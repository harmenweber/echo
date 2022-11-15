package ch.harmen.echo.request;

import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
class RequestToCreateRequestResultDtoTransformer
  implements Function<Request, CreateRequestResultDto> {

  @Override
  public CreateRequestResultDto apply(final Request request) {
    return new CreateRequestResultDto(request.id());
  }
}
