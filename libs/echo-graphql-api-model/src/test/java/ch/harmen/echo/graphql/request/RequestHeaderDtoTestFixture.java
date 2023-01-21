package ch.harmen.echo.graphql.request;

import com.github.javafaker.Faker;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public final class RequestHeaderDtoTestFixture {

  public static final int RANDOM_VALUES_MAX_SIZE = 3;

  private final Faker faker = new Faker();

  public RequestHeaderDto create() {
    return new RequestHeaderDto(getRandomName(), getRandomValues());
  }

  public String getRandomName() {
    return UUID.randomUUID().toString();
  }

  public List<String> getRandomValues() {
    return Stream
      .generate(UUID::randomUUID)
      .map(UUID::toString)
      .limit(this.faker.random().nextInt(RANDOM_VALUES_MAX_SIZE))
      .toList();
  }
}
