package ch.harmen.echo.rest.request;

import ch.harmen.echo.rest.request.CreateRequestResultDto;
import java.util.UUID;

public final class CreateRequestResultDtoTestFixture {

  public CreateRequestResultDto create() {
    return new CreateRequestResultDto(getRandomId());
  }

  public String getRandomId() {
    return UUID.randomUUID().toString();
  }
}
