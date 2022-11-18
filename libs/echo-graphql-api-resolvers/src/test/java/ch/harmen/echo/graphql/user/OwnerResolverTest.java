package ch.harmen.echo.graphql.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import ch.harmen.echo.GraphQlTestConfiguration;
import ch.harmen.echo.graphql.common.Edges;
import ch.harmen.echo.graphql.endpoint.EndPointEdgeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

@SpringBootTest(classes = GraphQlTestConfiguration.class)
final class OwnerResolverTest {

  static final String OWNER_WITH_ENDPOINTS_QUERY =
    """
      query ownerWithEndpoints($first: Int, $last: Int, $after: String, $before: String) {
        owner {
          id
          endpoints(first: $first, last: $last, after: $after, before: $before) {
            edges {
              cursor
              node {
                id
                apiKey
              }
            }
            pageInfo {
              startCursor
              endCursor
              hasNextPage
              hasPreviousPage
            }
          }
        }
      }""";

  @Autowired
  private HttpGraphQlTester graphQlTester;

  @Test
  void owner_whenCalledWithIdFieldOnly_returnsOwnerWithIdFieldOnly() {
    final var document =
      """
          {
          owner {
            id
          }
        }""";

    final var owner =
      this.graphQlTester.document(document)
        .execute()
        .path("owner")
        .entity(UserDto.class)
        .get();

    assertThat(owner).isNotNull();
    assertAll(
      () -> assertThat(owner.id()).isNotBlank(),
      () -> assertThat(owner.endpoints()).isNull()
    );
  }

  @Test
  void owner_whenCalledWithEndpointsField_returnsOwnerWithEndpoints() {
    final var owner =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .execute()
        .path("owner")
        .entity(UserDto.class)
        .get();

    assertThat(owner).isNotNull();
    assertThat(owner.id()).isNotBlank();
    assertThat(owner.endpoints()).isNotNull();

    /* If this assertion fails, you must ensure that the GraphQlTestConfiguration creates enough sample data. */
    final var edges = owner.endpoints().edges();
    assertThat(edges)
      .withFailMessage("This test requires existing endpoints.")
      .isNotEmpty();

    final var pageInfo = owner.endpoints().pageInfo();
    assertThat(pageInfo.startCursor())
      .isEqualTo(Edges.getFirst(edges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.endCursor())
      .isEqualTo(Edges.getLast(edges).map(EndPointEdgeDto::cursor));
  }

  @Test
  void owner_whenCalledWithEndpoints_andWithFirstAndAfterParameter_returnsCorrectEndpoints() {
    /* This Test expects at least 1 endpoint to be created upfront. */
    final var numberOfRequiredEdges = 1;

    final var unfilteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    /* If this assertion fails, you must ensure that the GraphQlTestConfiguration creates enough sample data. */
    final var unfilteredEdges = unfilteredEndpointsConnection.edges();
    assertThat(unfilteredEdges.size())
      .withFailMessage(
        "This test requires at least %d existing endpoints.",
        numberOfRequiredEdges
      )
      .isGreaterThanOrEqualTo(numberOfRequiredEdges);

    final var firstUnfilteredEdge = Edges
      .getFirst(unfilteredEdges)
      .orElseThrow();

    final var filteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .variable("first", unfilteredEdges.size() - 1)
        .variable("after", firstUnfilteredEdge.cursor())
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    final var filteredEdges = filteredEndpointsConnection.edges();
    assertThat(filteredEdges)
      .isEqualTo(unfilteredEdges.subList(1, unfilteredEdges.size()));

    final var pageInfo = filteredEndpointsConnection.pageInfo();
    assertThat(pageInfo.startCursor())
      .isEqualTo(Edges.getFirst(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.endCursor())
      .isEqualTo(Edges.getLast(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.hasPreviousPage()).isEqualTo(false);
    assertThat(pageInfo.hasNextPage())
      .isEqualTo(unfilteredEndpointsConnection.pageInfo().hasNextPage());
  }

  @Test
  void owner_whenCalledWithEndpoints_andWithFirstAndBeforeParameter_returnsCorrectEndpoints() {
    /* This Test expects at least 1 endpoint to be created upfront. */
    final var numberOfRequiredEdges = 1;

    final var unfilteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    /* If this assertion fails, you must ensure that the GraphQlTestConfiguration creates enough sample data. */
    final var unfilteredEdges = unfilteredEndpointsConnection.edges();
    assertThat(unfilteredEdges.size())
      .withFailMessage(
        "This test requires at least %d existing endpoints.",
        numberOfRequiredEdges
      )
      .isGreaterThanOrEqualTo(numberOfRequiredEdges);

    final var lastUnfilteredEdge = Edges.getLast(unfilteredEdges).orElseThrow();

    final var filteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .variable("first", unfilteredEdges.size() - 1)
        .variable("before", lastUnfilteredEdge.cursor())
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    final var filteredEdges = filteredEndpointsConnection.edges();
    assertThat(filteredEdges)
      .isEqualTo(unfilteredEdges.subList(0, unfilteredEdges.size() - 1));

    final var pageInfo = filteredEndpointsConnection.pageInfo();
    assertThat(pageInfo.startCursor())
      .isEqualTo(Edges.getFirst(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.endCursor())
      .isEqualTo(Edges.getLast(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.hasPreviousPage()).isEqualTo(false);
    assertThat(pageInfo.hasNextPage()).isEqualTo(false);
  }

  @Test
  void owner_whenCalledWithEndpoints_andWithFirstAfterAndBeforeParameter_returnsCorrectEndpoints() {
    /* This Test expects at least 2 endpoints to be created upfront. */
    final var numberOfRequiredEdges = 2;

    final var unfilteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    /* If this assertion fails, you must ensure that the GraphQlTestConfiguration creates enough sample data. */
    final var unfilteredEdges = unfilteredEndpointsConnection.edges();
    assertThat(unfilteredEdges.size())
      .withFailMessage(
        "This test requires at least %d existing endpoints.",
        numberOfRequiredEdges
      )
      .isGreaterThanOrEqualTo(numberOfRequiredEdges);

    final var firstUnfilteredEdge = Edges
      .getFirst(unfilteredEdges)
      .orElseThrow();
    final var lastUnfilteredEdge = Edges.getLast(unfilteredEdges).orElseThrow();

    final var filteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .variable("first", unfilteredEdges.size() - 2)
        .variable("after", firstUnfilteredEdge.cursor())
        .variable("before", lastUnfilteredEdge.cursor())
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    final var filteredEdges = filteredEndpointsConnection.edges();
    assertThat(filteredEdges)
      .isEqualTo(unfilteredEdges.subList(1, unfilteredEdges.size() - 1));

    final var pageInfo = filteredEndpointsConnection.pageInfo();
    assertThat(pageInfo.startCursor())
      .isEqualTo(Edges.getFirst(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.endCursor())
      .isEqualTo(Edges.getLast(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.hasPreviousPage()).isEqualTo(false);
    assertThat(pageInfo.hasNextPage()).isEqualTo(false);
  }

  @Test
  void owner_whenCalledWithEndpoints_andWithFirstParameterLessThanAvailableEndpoints_returnsCorrectEndpoints_andHasNextPageTrue() {
    /* This Test expects at least 4 endpoints to be created upfront. */
    final var numberOfRequiredEdges = 4;

    final var unfilteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    /* If this assertion fails, you must ensure that the GraphQlTestConfiguration creates enough sample data. */
    final var unfilteredEdges = unfilteredEndpointsConnection.edges();
    assertThat(unfilteredEdges.size())
      .withFailMessage(
        "This test requires at least %d existing endpoints.",
        numberOfRequiredEdges
      )
      .isGreaterThanOrEqualTo(numberOfRequiredEdges);

    final var firstUnfilteredEdge = unfilteredEdges.get(0);
    final var secondUnfilteredEdge = unfilteredEdges.get(1);
    final var fourthUnfilteredEdge = unfilteredEdges.get(3);

    final var filteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .variable("first", 1)
        .variable("after", firstUnfilteredEdge.cursor())
        .variable("before", fourthUnfilteredEdge.cursor())
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    final var filteredEdges = filteredEndpointsConnection.edges();
    assertThat(filteredEdges.size()).isEqualTo(1);
    assertThat(filteredEdges.get(0)).isEqualTo(secondUnfilteredEdge);

    final var pageInfo = filteredEndpointsConnection.pageInfo();
    assertThat(pageInfo.startCursor())
      .isEqualTo(Edges.getFirst(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.endCursor())
      .isEqualTo(Edges.getLast(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.hasPreviousPage()).isEqualTo(false);
    assertThat(pageInfo.hasNextPage()).isEqualTo(true);
  }

  @Test
  void owner_whenCalledWithEndpoints_andWithFirstParameterEqualToAvailableEndpoints_returnsCorrectEndpoints_andHasNextPageFalse() {
    /* This Test expects at least 4 endpoints to be created upfront. */
    final var numberOfRequiredEdges = 4;

    final var unfilteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    /* If this assertion fails, you must ensure that the GraphQlTestConfiguration creates enough sample data. */
    final var unfilteredEdges = unfilteredEndpointsConnection.edges();
    assertThat(unfilteredEdges.size())
      .withFailMessage(
        "This test requires at least %d existing endpoints.",
        numberOfRequiredEdges
      )
      .isGreaterThanOrEqualTo(numberOfRequiredEdges);

    final var firstUnfilteredEdge = unfilteredEdges.get(0);
    final var secondUnfilteredEdge = unfilteredEdges.get(1);
    final var thirdUnfilteredEdge = unfilteredEdges.get(2);
    final var fourthUnfilteredEdge = unfilteredEdges.get(3);

    final var filteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .variable("first", 2)
        .variable("after", firstUnfilteredEdge.cursor())
        .variable("before", fourthUnfilteredEdge.cursor())
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    final var filteredEdges = filteredEndpointsConnection.edges();
    assertThat(filteredEdges.size()).isEqualTo(2);
    assertThat(filteredEdges.get(0)).isEqualTo(secondUnfilteredEdge);
    assertThat(filteredEdges.get(1)).isEqualTo(thirdUnfilteredEdge);

    final var pageInfo = filteredEndpointsConnection.pageInfo();
    assertThat(pageInfo.startCursor())
      .isEqualTo(Edges.getFirst(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.endCursor())
      .isEqualTo(Edges.getLast(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.hasPreviousPage()).isEqualTo(false);
    assertThat(pageInfo.hasNextPage()).isEqualTo(false);
  }

  @Test
  void owner_whenCalledWithEndpoints_andWithLastAndBeforeParameter_returnsCorrectEndpoints() {
    /* This Test expects at least 1 endpoint to be created upfront. */
    final var numberOfRequiredEdges = 1;

    final var unfilteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    /* If this assertion fails, you must ensure that the GraphQlTestConfiguration creates enough sample data. */
    final var unfilteredEdges = unfilteredEndpointsConnection.edges();
    assertThat(unfilteredEdges.size())
      .withFailMessage(
        "This test requires at least %d existing endpoints.",
        numberOfRequiredEdges
      )
      .isGreaterThanOrEqualTo(numberOfRequiredEdges);

    final var lastUnfilteredEdge = Edges.getLast(unfilteredEdges).orElseThrow();

    final var filteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .variable("last", unfilteredEdges.size() - 1)
        .variable("before", lastUnfilteredEdge.cursor())
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    final var filteredEdges = filteredEndpointsConnection.edges();
    assertThat(filteredEdges)
      .isEqualTo(unfilteredEdges.subList(0, unfilteredEdges.size() - 1));

    final var pageInfo = filteredEndpointsConnection.pageInfo();
    assertThat(pageInfo.startCursor())
      .isEqualTo(Edges.getFirst(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.endCursor())
      .isEqualTo(Edges.getLast(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.hasPreviousPage()).isEqualTo(false);
    assertThat(pageInfo.hasNextPage()).isEqualTo(false);
  }

  @Test
  void owner_whenCalledWithEndpoints_andWithLastAndAfterParameter_returnsCorrectEndpoints() {
    /* This Test expects at least 1 endpoint to be created upfront. */
    final var numberOfRequiredEdges = 1;

    final var unfilteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    /* If this assertion fails, you must ensure that the GraphQlTestConfiguration creates enough sample data. */
    final var unfilteredEdges = unfilteredEndpointsConnection.edges();
    assertThat(unfilteredEdges.size())
      .withFailMessage(
        "This test requires at least %d existing endpoints.",
        numberOfRequiredEdges
      )
      .isGreaterThanOrEqualTo(numberOfRequiredEdges);

    final var firstUnfilteredEdge = Edges
      .getFirst(unfilteredEdges)
      .orElseThrow();

    final var filteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .variable("last", unfilteredEdges.size() - 1)
        .variable("after", firstUnfilteredEdge.cursor())
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    final var filteredEdges = filteredEndpointsConnection.edges();
    assertThat(filteredEdges)
      .isEqualTo(unfilteredEdges.subList(1, unfilteredEdges.size()));

    final var pageInfo = filteredEndpointsConnection.pageInfo();
    assertThat(pageInfo.startCursor())
      .isEqualTo(Edges.getFirst(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.endCursor())
      .isEqualTo(Edges.getLast(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.hasPreviousPage()).isEqualTo(false);
    assertThat(pageInfo.hasNextPage()).isEqualTo(false);
  }

  @Test
  void owner_whenCalledWithEndpoints_andWithLastAfterAndBeforeParameter_returnsCorrectEndpoints() {
    /* This Test expects at least 2 endpoints to be created upfront. */
    final var numberOfRequiredEdges = 2;

    final var unfilteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    /* If this assertion fails, you must ensure that the GraphQlTestConfiguration creates enough sample data. */
    final var unfilteredEdges = unfilteredEndpointsConnection.edges();
    assertThat(unfilteredEdges.size())
      .withFailMessage(
        "This test requires at least %d existing endpoints.",
        numberOfRequiredEdges
      )
      .isGreaterThanOrEqualTo(numberOfRequiredEdges);

    final var firstUnfilteredEdge = Edges
      .getFirst(unfilteredEdges)
      .orElseThrow();
    final var lastUnfilteredEdge = Edges.getLast(unfilteredEdges).orElseThrow();

    final var filteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .variable("last", unfilteredEdges.size() - 2)
        .variable("after", firstUnfilteredEdge.cursor())
        .variable("before", lastUnfilteredEdge.cursor())
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    final var filteredEdges = filteredEndpointsConnection.edges();
    assertThat(filteredEdges)
      .isEqualTo(unfilteredEdges.subList(1, unfilteredEdges.size() - 1));

    final var pageInfo = filteredEndpointsConnection.pageInfo();
    assertThat(pageInfo.startCursor())
      .isEqualTo(Edges.getFirst(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.endCursor())
      .isEqualTo(Edges.getLast(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.hasPreviousPage()).isEqualTo(false);
    assertThat(pageInfo.hasNextPage()).isEqualTo(false);
  }

  @Test
  void owner_whenCalledWithEndpoints_andWithLastParameterLessThanAvailableEndpoints_returnsCorrectEndpoints_andHasPreviousPageTrue() {
    /* This Test expects at least 4 endpoints to be created upfront. */
    final var numberOfRequiredEdges = 4;

    final var unfilteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    /* If this assertion fails, you must ensure that the GraphQlTestConfiguration creates enough sample data. */
    final var unfilteredEdges = unfilteredEndpointsConnection.edges();
    assertThat(unfilteredEdges.size())
      .withFailMessage(
        "This test requires at least %d existing endpoints.",
        numberOfRequiredEdges
      )
      .isGreaterThanOrEqualTo(numberOfRequiredEdges);

    final var firstUnfilteredEdge = unfilteredEdges.get(0);
    final var thirdUnfilteredEdge = unfilteredEdges.get(2);
    final var fourthUnfilteredEdge = unfilteredEdges.get(3);

    final var filteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .variable("last", 1)
        .variable("after", firstUnfilteredEdge.cursor())
        .variable("before", fourthUnfilteredEdge.cursor())
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    final var filteredEdges = filteredEndpointsConnection.edges();
    assertThat(filteredEdges.size()).isEqualTo(1);
    assertThat(filteredEdges.get(0)).isEqualTo(thirdUnfilteredEdge);

    final var pageInfo = filteredEndpointsConnection.pageInfo();
    assertThat(pageInfo.startCursor())
      .isEqualTo(Edges.getFirst(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.endCursor())
      .isEqualTo(Edges.getLast(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.hasPreviousPage()).isEqualTo(true);
    assertThat(pageInfo.hasNextPage()).isEqualTo(false);
  }

  @Test
  void owner_whenCalledWithEndpoints_andWithLastParameterEqualToAvailableEndpoints_returnsCorrectEndpoints_andHasPreviousPageFalse() {
    /* This Test expects at least 4 endpoints to be created upfront. */
    final var numberOfRequiredEdges = 4;

    final var unfilteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    /* If this assertion fails, you must ensure that the GraphQlTestConfiguration creates enough sample data. */
    final var unfilteredEdges = unfilteredEndpointsConnection.edges();
    assertThat(unfilteredEdges.size())
      .withFailMessage(
        "This test requires at least %d existing endpoints.",
        numberOfRequiredEdges
      )
      .isGreaterThanOrEqualTo(numberOfRequiredEdges);

    final var firstUnfilteredEdge = unfilteredEdges.get(0);
    final var secondUnfilteredEdge = unfilteredEdges.get(1);
    final var thirdUnfilteredEdge = unfilteredEdges.get(2);
    final var fourthUnfilteredEdge = unfilteredEdges.get(3);

    final var filteredEndpointsConnection =
      this.graphQlTester.document(OWNER_WITH_ENDPOINTS_QUERY)
        .variable("last", 2)
        .variable("after", firstUnfilteredEdge.cursor())
        .variable("before", fourthUnfilteredEdge.cursor())
        .execute()
        .path("owner.endpoints")
        .entity(OwnerEndpointsConnectionDto.class)
        .get();

    final var filteredEdges = filteredEndpointsConnection.edges();
    assertThat(filteredEdges.size()).isEqualTo(2);
    assertThat(filteredEdges.get(0)).isEqualTo(secondUnfilteredEdge);
    assertThat(filteredEdges.get(1)).isEqualTo(thirdUnfilteredEdge);

    final var pageInfo = filteredEndpointsConnection.pageInfo();
    assertThat(pageInfo.startCursor())
      .isEqualTo(Edges.getFirst(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.endCursor())
      .isEqualTo(Edges.getLast(filteredEdges).map(EndPointEdgeDto::cursor));
    assertThat(pageInfo.hasPreviousPage()).isEqualTo(false);
    assertThat(pageInfo.hasNextPage()).isEqualTo(false);
  }
}
