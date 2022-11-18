package ch.harmen.echo.graphql.user;

import com.github.javafaker.Faker;
import java.util.UUID;

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

  public OwnerEndpointsConnectionDto getRandomEndpoints() {
    return this.ownerEndpointsConnectionDtoTestFixture.create();
  }
}
