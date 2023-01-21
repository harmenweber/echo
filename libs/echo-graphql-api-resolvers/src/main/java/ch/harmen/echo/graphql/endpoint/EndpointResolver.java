package ch.harmen.echo.graphql.endpoint;

import ch.harmen.echo.endpoint.EndpointService;
import ch.harmen.echo.graphql.common.BackwardPagingDto;
import ch.harmen.echo.graphql.common.Edges;
import ch.harmen.echo.graphql.common.ForwardPagingDto;
import ch.harmen.echo.graphql.common.PageInfoDtoFactory;
import ch.harmen.echo.graphql.common.PagingDto;
import ch.harmen.echo.request.RequestService;
import ch.harmen.echo.user.CurrentUserContextSupplier;
import java.util.Objects;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.Arguments;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@SchemaMapping(typeName = "EndpointDto")
class EndpointResolver {

  public static final int REQUESTS_DEFAULT_FIRST = 100;

  private final CurrentUserContextSupplier currentUserContextSupplier;
  private final EndpointService endpointService;
  private final RequestService requestService;
  private final EndpointToEndpointDtoTransformer endpointToEndpointDtoTransformer;
  private final ReceiveTimeAndIdToCursorConverter receiveTimeAndIdToCursorConverter;
  private final RequestToEndpointRequestDtoTransformer requestToEndpointRequestDtoTransformer;
  private final EndpointRequestDtoToEndpointRequestEdgeDtoTransformer endpointRequestDtoToEndpointRequestEdgeDtoTransformer;
  private final PageInfoDtoFactory pageInfoDtoFactory;

  EndpointResolver(
    final CurrentUserContextSupplier currentUserContextSupplier,
    final EndpointService endpointService,
    final RequestService requestService,
    final EndpointToEndpointDtoTransformer endpointToEndpointDtoTransformer,
    final ReceiveTimeAndIdToCursorConverter receiveTimeAndIdToCursorConverter,
    final RequestToEndpointRequestDtoTransformer requestToEndpointRequestDtoTransformer,
    final EndpointRequestDtoToEndpointRequestEdgeDtoTransformer endpointRequestDtoToEndpointRequestEdgeDtoTransformer,
    final PageInfoDtoFactory pageInfoDtoFactory
  ) {
    this.currentUserContextSupplier =
      Objects.requireNonNull(currentUserContextSupplier);
    this.endpointService = Objects.requireNonNull(endpointService);
    this.requestService = Objects.requireNonNull(requestService);
    this.endpointToEndpointDtoTransformer =
      Objects.requireNonNull(endpointToEndpointDtoTransformer);
    this.receiveTimeAndIdToCursorConverter =
      Objects.requireNonNull(receiveTimeAndIdToCursorConverter);
    this.requestToEndpointRequestDtoTransformer =
      Objects.requireNonNull(requestToEndpointRequestDtoTransformer);
    this.endpointRequestDtoToEndpointRequestEdgeDtoTransformer =
      Objects.requireNonNull(
        endpointRequestDtoToEndpointRequestEdgeDtoTransformer
      );
    this.pageInfoDtoFactory = Objects.requireNonNull(pageInfoDtoFactory);
  }

  @QueryMapping
  Mono<EndpointDto> endpoint(@Argument final String id) {
    Objects.requireNonNull(id);
    return this.endpointService.findByOwnerAndId(
        this.currentUserContextSupplier.get().id(),
        id
      )
      .map(this.endpointToEndpointDtoTransformer);
  }

  @SchemaMapping
  Mono<EndpointRequestsConnectionDto> requests(
    final EndpointDto endpoint,
    @Arguments final PagingDto paging
  ) {
    Objects.requireNonNull(endpoint);
    Objects.requireNonNull(paging);

    if (paging.first().isPresent()) {
      return findFirstRequests(
        endpoint,
        new ForwardPagingDto(
          paging.before(),
          paging.after(),
          paging.first().get()
        )
      );
    } else if (paging.last().isPresent()) {
      return findLastRequests(
        endpoint,
        new BackwardPagingDto(
          paging.before(),
          paging.after(),
          paging.last().get()
        )
      );
    } else {
      return findFirstRequests(
        endpoint,
        new ForwardPagingDto(
          paging.before(),
          paging.after(),
          REQUESTS_DEFAULT_FIRST
        )
      );
    }
  }

  private Mono<EndpointRequestsConnectionDto> findFirstRequests(
    final EndpointDto endpoint,
    final ForwardPagingDto forwardPaging
  ) {
    return this.requestService.findFirstByEndpoint(
        endpoint.id(),
        forwardPaging.first() + 1,
        forwardPaging
          .before()
          .map(
            this.receiveTimeAndIdToCursorConverter::cursorToReceiveTimeAndId
          ),
        forwardPaging
          .after()
          .map(this.receiveTimeAndIdToCursorConverter::cursorToReceiveTimeAndId)
      )
      .map(this.requestToEndpointRequestDtoTransformer)
      .map(this.endpointRequestDtoToEndpointRequestEdgeDtoTransformer)
      .collectList()
      .map(edges -> {
        final var hasNextPage = edges.size() > forwardPaging.first();
        final var limitedEdges = Edges.takeFirst(edges, forwardPaging.first());
        return new EndpointRequestsConnectionDto(
          limitedEdges,
          this.pageInfoDtoFactory.createForForwardPaging(
              limitedEdges,
              hasNextPage
            )
        );
      });
  }

  private Mono<EndpointRequestsConnectionDto> findLastRequests(
    final EndpointDto endpoint,
    final BackwardPagingDto backwardPaging
  ) {
    return this.requestService.findLastByEndpoint(
        endpoint.id(),
        backwardPaging.last() + 1,
        backwardPaging
          .before()
          .map(
            this.receiveTimeAndIdToCursorConverter::cursorToReceiveTimeAndId
          ),
        backwardPaging
          .after()
          .map(this.receiveTimeAndIdToCursorConverter::cursorToReceiveTimeAndId)
      )
      .map(this.requestToEndpointRequestDtoTransformer)
      .map(this.endpointRequestDtoToEndpointRequestEdgeDtoTransformer)
      .collectList()
      .map(edges -> {
        final var hasPreviousPage = edges.size() > backwardPaging.last();
        final var limitedEdges = Edges.takeLast(edges, backwardPaging.last());
        return new EndpointRequestsConnectionDto(
          limitedEdges,
          this.pageInfoDtoFactory.createForBackwardPaging(
              limitedEdges,
              hasPreviousPage
            )
        );
      });
  }
}
