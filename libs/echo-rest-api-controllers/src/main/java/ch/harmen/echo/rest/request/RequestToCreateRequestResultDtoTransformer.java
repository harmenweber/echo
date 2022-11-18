package ch.harmen.echo.rest.request;

import ch.harmen.echo.request.Request;
import ch.harmen.echo.rest.request.CreateRequestResultDto;
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
