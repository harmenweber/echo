package ch.harmen.echo.request;

import java.time.Instant;

public final class ReceiveTimeAndIdTestFixture {

  private final RequestTestFixture requestTestFixture = new RequestTestFixture();

  public ReceiveTimeAndId create() {
    return new ReceiveTimeAndId(getRandomReceiveTime(), getRandomId());
  }

  public Instant getRandomReceiveTime() {
    return this.requestTestFixture.getRandomReceiveTime();
  }

  public String getRandomId() {
    return this.requestTestFixture.getRandomId();
  }
}
