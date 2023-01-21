package ch.harmen.echo.request;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

final class RequestList {

  private final AtomicInteger counter = new AtomicInteger(0);
  private final NavigableSet<Request> requests = new ConcurrentSkipListSet<>(
    Request.COMPARATOR
  );
  private final int requestLimit;

  RequestList(final int requestLimit) {
    this.requestLimit = requestLimit;
  }

  void add(final Request request) {
    if (
      this.requests.add(request) &&
      this.counter.incrementAndGet() > this.requestLimit
    ) {
      this.requests.pollLast();
      this.counter.decrementAndGet();
    }
  }

  /**
   * Removes the request from the list.
   *
   * @param request The request that must be removed.
   * @return The removed request, or {@code null} if the request was not in the list.
   */
  Request remove(final Request request) {
    if (this.requests.remove(request)) {
      this.counter.decrementAndGet();
      return request;
    } else {
      return null;
    }
  }

  NavigableSet<Request> getRequests() {
    return Collections.unmodifiableNavigableSet(requests);
  }

  int size() {
    return counter.get();
  }
}
