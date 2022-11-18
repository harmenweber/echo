package ch.harmen.echo.graphql.user;

import ch.harmen.echo.endpoint.EndpointService;
import ch.harmen.echo.graphql.common.BackwardPagingDto;
import ch.harmen.echo.graphql.common.Edges;
import ch.harmen.echo.graphql.common.ForwardPagingDto;
import ch.harmen.echo.graphql.common.PageInfoDtoFactory;
import ch.harmen.echo.graphql.common.PagingDto;
import ch.harmen.echo.graphql.endpoint.EndpointCursorConverter;
import ch.harmen.echo.graphql.endpoint.EndpointDtoToEndpointEdgeDtoTransformer;
import ch.harmen.echo.graphql.endpoint.EndpointToEndpointDtoTransformer;
import ch.harmen.echo.user.CurrentUserContextSupplier;
import java.util.Objects;
import org.springframework.graphql.data.method.annotation.Arguments;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@SchemaMapping(typeName = "UserDto")
class OwnerResolver {

  public static final int DEFAULT_FIRST = 100;
  private final CurrentUserContextSupplier currentUserContextSupplier;
  private final EndpointService endpointService;
  private final EndpointToEndpointDtoTransformer endpointToEndpointDtoTransformer;
  private final EndpointDtoToEndpointEdgeDtoTransformer endpointDtoToEndpointEdgeDtoTransformer;
  private final PageInfoDtoFactory pageInfoDtoFactory;
  private final EndpointCursorConverter endpointCursorConverter;

  OwnerResolver(
    CurrentUserContextSupplier currentUserContextSupplier,
    EndpointService endpointService,
    EndpointToEndpointDtoTransformer endpointToEndpointDtoTransformer,
    EndpointDtoToEndpointEdgeDtoTransformer endpointDtoToEndpointEdgeDtoTransformer,
    PageInfoDtoFactory pageInfoDtoFactory,
    EndpointCursorConverter endpointCursorConverter
  ) {
    this.currentUserContextSupplier =
      Objects.requireNonNull(currentUserContextSupplier);
    this.endpointService = Objects.requireNonNull(endpointService);
    this.endpointToEndpointDtoTransformer =
      Objects.requireNonNull(endpointToEndpointDtoTransformer);
    this.endpointDtoToEndpointEdgeDtoTransformer =
      Objects.requireNonNull(endpointDtoToEndpointEdgeDtoTransformer);
    this.pageInfoDtoFactory = Objects.requireNonNull(pageInfoDtoFactory);
    this.endpointCursorConverter =
      Objects.requireNonNull(endpointCursorConverter);
  }

  @QueryMapping
  Mono<UserDto> owner() {
    return Mono
      .just(this.currentUserContextSupplier.get().id())
      .map(id -> new UserDto(id, null));
  }

  @SchemaMapping
  Mono<OwnerEndpointsConnectionDto> endpoints(
    final UserDto owner,
    @Arguments final PagingDto paging
  ) {
    if (paging.first().isPresent()) {
      return findFirstEndpoints(
        owner,
        new ForwardPagingDto(
          paging.before(),
          paging.after(),
          paging.first().get()
        )
      );
    } else if (paging.last().isPresent()) {
      return findLastEndpoints(
        owner,
        new BackwardPagingDto(
          paging.before(),
          paging.after(),
          paging.last().get()
        )
      );
    } else {
      return findFirstEndpoints(
        owner,
        new ForwardPagingDto(paging.before(), paging.after(), 100)
      );
    }
  }

  private Mono<OwnerEndpointsConnectionDto> findFirstEndpoints(
    final UserDto owner,
    final ForwardPagingDto forwardPaging
  ) {
    return this.endpointService.findFirstByOwner(
        owner.id(),
        forwardPaging.first() + 1,
        forwardPaging
          .before()
          .map(this.endpointCursorConverter::cursorToEndpointId),
        forwardPaging
          .after()
          .map(this.endpointCursorConverter::cursorToEndpointId)
      )
      .map(this.endpointToEndpointDtoTransformer)
      .map(this.endpointDtoToEndpointEdgeDtoTransformer)
      .collectList()
      .map(edges -> {
        final var hasNextPage = edges.size() > forwardPaging.first();
        final var limitedEdges = Edges.takeFirst(edges, forwardPaging.first());
        return new OwnerEndpointsConnectionDto(
          limitedEdges,
          this.pageInfoDtoFactory.createForForwardPaging(
              limitedEdges,
              hasNextPage
            )
        );
      });
  }

  private Mono<OwnerEndpointsConnectionDto> findLastEndpoints(
    final UserDto owner,
    final BackwardPagingDto backwardPaging
  ) {
    return this.endpointService.findLastByOwner(
        owner.id(),
        backwardPaging.last() + 1,
        backwardPaging
          .before()
          .map(this.endpointCursorConverter::cursorToEndpointId),
        backwardPaging
          .after()
          .map(this.endpointCursorConverter::cursorToEndpointId)
      )
      .map(this.endpointToEndpointDtoTransformer)
      .map(this.endpointDtoToEndpointEdgeDtoTransformer)
      .collectList()
      .map(edges -> {
        final var hasPreviousPage = edges.size() > backwardPaging.last();
        final var limitedEdges = Edges.takeLast(edges, backwardPaging.last());
        return new OwnerEndpointsConnectionDto(
          limitedEdges,
          this.pageInfoDtoFactory.createForBackwardPaging(
              limitedEdges,
              hasPreviousPage
            )
        );
      });
  }
}
