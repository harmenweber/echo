package ch.harmen.echo.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

final class RequestListTest {

  private final RequestTestFixture requestTestFixture = new RequestTestFixture();

  @Test
  void add_addsTheRequestToTheList_ifRequestLimitHasNotBeenReachedYet() {
    final int requestLimit = 10;
    final RequestList requestList = new RequestList(requestLimit);
    for (int i = 1; i <= requestLimit; i++) {
      final Request request = this.requestTestFixture.create();
      requestList.add(request);
      assertThat(requestList.getRequests()).contains(request);
    }
  }

  @Test
  void add_respectsRequestLimit() {
    final int requestLimit = 10;
    final RequestList requestList = new RequestList(requestLimit);
    for (int i = 1; i <= requestLimit * 2; i++) {
      requestList.add(this.requestTestFixture.create());
      assertThat(requestList.size()).isEqualTo(Math.min(i, requestLimit));
    }
  }

  @Test
  void add_discardsOldestRequest_ifRequestListIsReached_andNewlyAddedRequestIsMoreRecentThanAllRequestsInRequestList() {
    // Fill request list up to request limit.
    final int requestLimit = 10;
    final RequestList requestList = new RequestList(requestLimit);
    for (int i = 1; i <= requestLimit * 2; i++) {
      requestList.add(this.requestTestFixture.create());
    }

    final Request mostRecentRequest = requestList.getRequests().first();
    final Request oldestRequest = requestList.getRequests().last();

    // Add another request with a more recent receiveTime.
    final Request request = new Request(
      this.requestTestFixture.getRandomId(),
      this.requestTestFixture.getRandomEndpointId(),
      mostRecentRequest.receiveTime().plusSeconds(1),
      this.requestTestFixture.getRandomUri(),
      this.requestTestFixture.getRandomMethod(),
      this.requestTestFixture.getRandomHeaders(),
      Optional.of(this.requestTestFixture.getRandomBody())
    );
    requestList.add(request);

    // Assert request got added.
    assertAll(
      () -> assertThat(requestList.getRequests()).contains(request),
      () -> assertThat(requestList.getRequests()).contains(mostRecentRequest),
      () -> assertThat(requestList.getRequests()).doesNotContain(oldestRequest)
    );
  }

  @Test
  void add_discardsOldestRequest_ifRequestListIsReached_andNewlyAddedRequestIsBetweenMostRecentAndOldestRequestInRequestList() {
    // Fill request list up to request limit.
    final int requestLimit = 10;
    final RequestList requestList = new RequestList(requestLimit);
    for (int i = 1; i <= requestLimit * 2; i++) {
      requestList.add(this.requestTestFixture.create());
    }

    final Request mostRecentRequest = requestList.getRequests().first();
    final Request oldestRequest = requestList.getRequests().last();

    // Add another request with a receiveTime older than all the receiveTimes in the request list.
    final Request request = new Request(
      this.requestTestFixture.getRandomId(),
      this.requestTestFixture.getRandomEndpointId(),
      getInstantBetween(
        oldestRequest.receiveTime(),
        mostRecentRequest.receiveTime()
      ),
      this.requestTestFixture.getRandomUri(),
      this.requestTestFixture.getRandomMethod(),
      this.requestTestFixture.getRandomHeaders(),
      Optional.of(this.requestTestFixture.getRandomBody())
    );
    requestList.add(request);

    // Assert request got discarded.
    assertAll(
      () -> assertThat(requestList.getRequests()).contains(request),
      () -> assertThat(requestList.getRequests()).contains(mostRecentRequest),
      () -> assertThat(requestList.getRequests()).doesNotContain(oldestRequest)
    );
  }

  private static Instant getInstantBetween(
    Instant oldestReceiveTime,
    Instant mostRecentReceiveTime
  ) {
    return oldestReceiveTime.plus(
      Duration.between(oldestReceiveTime, mostRecentReceiveTime).dividedBy(2)
    );
  }

  @Test
  void add_discardsOldestRequest_ifRequestListIsReached_andNewlyAddedRequestIsOlderThanAllRequestsInRequestList() {
    // Fill request list up to request limit.
    final int requestLimit = 10;
    final RequestList requestList = new RequestList(requestLimit);
    for (int i = 1; i <= requestLimit * 2; i++) {
      requestList.add(this.requestTestFixture.create());
    }

    final Request mostRecentRequest = requestList.getRequests().first();
    final Request oldestRequest = requestList.getRequests().last();

    // Add another request with a receiveTime older than all the receiveTimes in the request list.
    final Request request = new Request(
      this.requestTestFixture.getRandomId(),
      this.requestTestFixture.getRandomEndpointId(),
      oldestRequest.receiveTime().minusSeconds(1),
      this.requestTestFixture.getRandomUri(),
      this.requestTestFixture.getRandomMethod(),
      this.requestTestFixture.getRandomHeaders(),
      Optional.of(this.requestTestFixture.getRandomBody())
    );
    requestList.add(request);

    // Assert request got discarded.
    assertAll(
      () -> assertThat(requestList.getRequests()).doesNotContain(request),
      () -> assertThat(requestList.getRequests()).contains(mostRecentRequest),
      () -> assertThat(requestList.getRequests()).contains(oldestRequest)
    );
  }

  @Test
  void getRequests_returnsRequestsSortedByReceiveTimeDescending() {
    // Create some requests.
    final int requestLimit = 10;
    final List<Request> requests = Stream
      .generate(this.requestTestFixture::create)
      .limit(requestLimit)
      .toList();

    // Shuffle the requests so their receiveTime is not sorted.
    final List<Request> shuffledRequests = new ArrayList<>(requests);
    Collections.shuffle(shuffledRequests);

    // Fill request list up to request limit.
    final RequestList requestList = new RequestList(requestLimit);
    shuffledRequests.forEach(requestList::add);

    // Create the expected result.
    List<Request> sortedRequests = shuffledRequests
      .stream()
      .sorted(RequestList.REQUESTS_BY_RECEIVE_TIME_DESC)
      .toList();

    assertThat(requestList.getRequests().stream().toList())
      .isEqualTo(sortedRequests);
  }

  @Test
  void remove_returnsTheRequest_ifRemovedRequestIsContainedInRequestList() {
    final int requestLimit = 10;
    final RequestList requestList = new RequestList(requestLimit);

    final Request request = this.requestTestFixture.create();
    requestList.add(request);

    final Request removalResult = requestList.remove(request);
    assertAll(
      () -> assertThat(removalResult).isEqualTo(request),
      () -> assertThat(requestList.getRequests()).doesNotContain(request)
    );
  }

  @Test
  void remove_returnsNull_ifRemovedRequestIsNotContainedInRequestList() {
    final int requestLimit = 10;
    final RequestList requestList = new RequestList(requestLimit);

    final Request request = this.requestTestFixture.create();

    final Request removalResult = requestList.remove(request);
    assertAll(
      () -> assertThat(removalResult).isNull(),
      () -> assertThat(requestList.getRequests()).doesNotContain(request)
    );
  }
}
