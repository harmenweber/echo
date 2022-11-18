package ch.harmen.echo.graphql.user;

import com.github.javafaker.Faker;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public final class UserDtoTestFixture {

  public static final int RANDOM_ENDPOINTS_MIN_COUNT = 1;
  public static final int RANDOM_ENDPOINTS_MAX_COUNT = 3;

  private final Faker faker = new Faker();
  private final OwnerEndpointsConnectionDtoTestFixture ownerEndpointsConnectionDtoTestFixture = new OwnerEndpointsConnectionDtoTestFixture();

  public UserDto create() {
    return new UserDto(getRandomId(), getRandomEndpoints());
  }

  public String getRandomId() {
    return UUID.randomUUID().toString();
  }

  public List<OwnerEndpointsConnectionDto> getRandomEndpoints() {
    return Stream
      .generate(this.ownerEndpointsConnectionDtoTestFixture::create)
      .limit(
        this.faker.random()
          .nextInt(RANDOM_ENDPOINTS_MIN_COUNT, RANDOM_ENDPOINTS_MAX_COUNT)
      )
      .toList();
  }
}
