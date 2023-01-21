package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.graphql.request.RequestHeaderDto;
import ch.harmen.echo.request.RequestTestFixture;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.util.Base64Utils;

public final class EndpointRequestDtoTestFixture {

  private final RequestTestFixture requestTestFixture = new RequestTestFixture();

  public EndpointRequestDto create() {
    return new EndpointRequestDto(
      getRandomId(),
      getRandomReceiveTime(),
      getRandomUri(),
      getRandomMethod(),
      getRandomHeaders(),
      Optional.of(getRandomBase64EncodedBody())
    );
  }

  public String getRandomId() {
    return this.requestTestFixture.getRandomId();
  }

  public String getRandomReceiveTime() {
    return this.requestTestFixture.getRandomReceiveTime().toString();
  }

  public String getRandomUri() {
    return this.requestTestFixture.getRandomUri().toString();
  }

  public String getRandomMethod() {
    return this.requestTestFixture.getRandomMethod().name();
  }

  private List<RequestHeaderDto> getRandomHeaders() {
    return this.requestTestFixture.getRandomHeaders()
      .entrySet()
      .stream()
      .map(entry -> new RequestHeaderDto(entry.getKey(), entry.getValue()))
      .collect(Collectors.toList());
  }

  private String getRandomBase64EncodedBody() {
    return Base64Utils.encodeToString(this.requestTestFixture.getRandomBody());
  }
}
