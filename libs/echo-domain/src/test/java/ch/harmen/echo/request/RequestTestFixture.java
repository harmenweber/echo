package ch.harmen.echo.request;

import com.github.javafaker.Faker;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public final class RequestTestFixture {

  public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
  public static final Charset[] RANDOM_CHARSET_VALUES = new Charset[] {
    StandardCharsets.UTF_8,
    StandardCharsets.ISO_8859_1,
    StandardCharsets.US_ASCII,
    StandardCharsets.UTF_16,
  };
  private final Faker faker = new Faker();

  public Request create() {
    final String endpointId = getRandomEndpointId();
    final Charset charset = getRandomCharset();
    return new Request(
      getRandomId(),
      endpointId,
      getRandomReceiveTime(),
      getRandomUri(endpointId),
      getRandomMethod(),
      getRandomHeaders(charset),
      Optional.of(getRandomBody(charset))
    );
  }

  public String getRandomId() {
    return UUID.randomUUID().toString();
  }

  public String getRandomEndpointId() {
    return UUID.randomUUID().toString();
  }

  public Instant getRandomReceiveTime() {
    return Instant.now();
  }

  public URI getRandomUri() {
    return getRandomUri(getRandomEndpointId());
  }

  public URI getRandomUri(final String endpointId) {
    return URI.create(
      (RequestConstants.REQUESTS_PATH).replace(
          "{" + RequestConstants.ENDPOINT_ID_PATH_VARIABLE + "}",
          endpointId
        )
    );
  }

  public HttpMethod getRandomMethod() {
    return faker.options().option(HttpMethod.class);
  }

  public HttpHeaders getRandomHeaders() {
    return getRandomHeaders(DEFAULT_CHARSET);
  }

  public HttpHeaders getRandomHeaders(final Charset charset) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(
      new MediaType(MediaType.TEXT_PLAIN, DEFAULT_CHARSET)
    );
    httpHeaders.set("x-uuid", UUID.randomUUID().toString());
    return httpHeaders;
  }

  public byte[] getRandomBody() {
    return getRandomBody(DEFAULT_CHARSET);
  }

  public byte[] getRandomBody(final Charset charset) {
    return UUID.randomUUID().toString().getBytes(charset);
  }

  private Charset getRandomCharset() {
    return faker.options().option(RANDOM_CHARSET_VALUES);
  }
}
