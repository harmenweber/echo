package ch.harmen.echo.request;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public final class RequestTestFixture {

  private static final Random RANDOM = new Random(System.currentTimeMillis());
  private static final Charset[] CHARSETS = new Charset[] {
    StandardCharsets.UTF_8,
    StandardCharsets.ISO_8859_1,
    StandardCharsets.US_ASCII,
    StandardCharsets.UTF_16,
  };
  private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

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
      getRandomBody(charset)
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
    return URI.create("/endpoints/%s/requests".formatted(endpointId));
  }

  public HttpMethod getRandomMethod() {
    final HttpMethod[] httpMethods = HttpMethod.values();
    return httpMethods[RANDOM.nextInt(httpMethods.length)];
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
    return CHARSETS[RANDOM.nextInt(CHARSETS.length)];
  }
}
