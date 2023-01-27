package ch.harmen.echo.request;

import java.util.Objects;

public class ApiKeyIncorrectException extends RuntimeException {

  private final String endpointId;
  private final String apiKey;

  public ApiKeyIncorrectException(
    final String endpointId,
    final String apiKey
  ) {
    super(
      "API key %s incorrect for endpoint %s.".formatted(apiKey, endpointId)
    );
    this.endpointId = Objects.requireNonNull(endpointId);
    this.apiKey = apiKey;
  }

  public String getEndpointId() {
    return endpointId;
  }

  public String getApiKey() {
    return apiKey;
  }
}
