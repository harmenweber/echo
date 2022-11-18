package ch.harmen.echo.rest.request;

import ch.harmen.echo.request.RequestTestFixture;
import ch.harmen.echo.rest.request.RequestDto;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.util.Base64Utils;

public final class RequestDtoTestFixture {

  private final RequestTestFixture requestTestFixture = new RequestTestFixture();

  public RequestDto create() {
    final String endpointId = getRandomEndpointId();
    return new RequestDto(
      getRandomId(),
      endpointId,
      getRandomReceiveTime(),
      getRandomUri(endpointId),
      getRandomMethod(),
      getRandomHeaders(),
      Optional.of(getRandomBase64EncodedBody())
    );
  }

  public String getRandomId() {
    return this.requestTestFixture.getRandomId();
  }

  public String getRandomEndpointId() {
    return this.requestTestFixture.getRandomEndpointId();
  }

  public Instant getRandomReceiveTime() {
    return this.requestTestFixture.getRandomReceiveTime();
  }

  public URI getRandomUri() {
    return this.requestTestFixture.getRandomUri();
  }

  public URI getRandomUri(final String endpointId) {
    return this.requestTestFixture.getRandomUri(endpointId);
  }

  public String getRandomMethod() {
    return this.requestTestFixture.getRandomMethod().name();
  }

  private Map<String, List<String>> getRandomHeaders() {
    return this.requestTestFixture.getRandomHeaders();
  }

  private String getRandomBase64EncodedBody() {
    return Base64Utils.encodeToString(this.requestTestFixture.getRandomBody());
  }
}
