package ch.harmen.echo.graphql.common;

import ch.harmen.echo.graphql.common.PageInfoDto;
import com.github.javafaker.Faker;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import org.springframework.util.Base64Utils;

public final class PageInfoDtoTestFixture {

  private final Faker faker = new Faker();

  public PageInfoDto create() {
    return new PageInfoDto(
      Optional.of(getRandomStartCursor()),
      Optional.of(getRandomEndCursor()),
      getRandomHasPreviousPage(),
      getRandomHasNextPage()
    );
  }

  public String getRandomStartCursor() {
    return getRandomCursor();
  }

  public String getRandomEndCursor() {
    return getRandomCursor();
  }

  public String getRandomCursor() {
    return Base64Utils.encodeToString(
      UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)
    );
  }

  public boolean getRandomHasPreviousPage() {
    return faker.bool().bool();
  }

  public boolean getRandomHasNextPage() {
    return faker.bool().bool();
  }
}
