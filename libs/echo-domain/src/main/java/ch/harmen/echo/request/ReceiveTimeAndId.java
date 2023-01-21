package ch.harmen.echo.request;

import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;

public record ReceiveTimeAndId(Instant receiveTime, String id) {
  public static final Comparator<ReceiveTimeAndId> COMPARATOR = Comparator
    .comparing(ReceiveTimeAndId::receiveTime)
    .thenComparing(ReceiveTimeAndId::id)
    .reversed();

  public ReceiveTimeAndId {
    Objects.requireNonNull(receiveTime);
    Objects.requireNonNull(id);
  }

  public ReceiveTimeAndId(final ReceiveTimeAndId original) {
    this(original.receiveTime, original.id);
  }
}
