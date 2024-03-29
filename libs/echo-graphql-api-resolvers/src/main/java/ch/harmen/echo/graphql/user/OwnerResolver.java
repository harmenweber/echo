package ch.harmen.echo.graphql.user;

import ch.harmen.echo.endpoint.EndpointService;
import ch.harmen.echo.graphql.common.BackwardPagingDto;
import ch.harmen.echo.graphql.common.Edges;
import ch.harmen.echo.graphql.common.ForwardPagingDto;
import ch.harmen.echo.graphql.common.PageInfoDtoFactory;
import ch.harmen.echo.graphql.common.PagingDto;
import ch.harmen.echo.graphql.common.StringToCursorConverter;
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

  public static final int ENDPOINTS_DEFAULT_FIRST = 100;

  private final CurrentUserContextSupplier currentUserContextSupplier;
  private final EndpointService endpointService;
  private final EndpointToOwnerEndpointDtoTransformer endpointToOwnerEndpointDtoTransformer;
  private final OwnerEndpointDtoToOwnerEndpointEdgeDtoTransformer ownerEndpointDtoToOwnerEndpointEdgeDtoTransformer;
  private final PageInfoDtoFactory pageInfoDtoFactory;
  private final StringToCursorConverter stringToCursorConverter;

  OwnerResolver(
    final CurrentUserContextSupplier currentUserContextSupplier,
    final EndpointService endpointService,
    final EndpointToOwnerEndpointDtoTransformer endpointToOwnerEndpointDtoTransformer,
    final OwnerEndpointDtoToOwnerEndpointEdgeDtoTransformer ownerEndpointDtoToOwnerEndpointEdgeDtoTransformer,
    final PageInfoDtoFactory pageInfoDtoFactory,
    final StringToCursorConverter stringToCursorConverter
  ) {
    this.currentUserContextSupplier =
      Objects.requireNonNull(currentUserContextSupplier);
    this.endpointService = Objects.requireNonNull(endpointService);
    this.endpointToOwnerEndpointDtoTransformer =
      Objects.requireNonNull(endpointToOwnerEndpointDtoTransformer);
    this.ownerEndpointDtoToOwnerEndpointEdgeDtoTransformer =
      Objects.requireNonNull(ownerEndpointDtoToOwnerEndpointEdgeDtoTransformer);
    this.pageInfoDtoFactory = Objects.requireNonNull(pageInfoDtoFactory);
    this.stringToCursorConverter =
      Objects.requireNonNull(stringToCursorConverter);
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
    Objects.requireNonNull(owner);
    Objects.requireNonNull(paging);

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
        new ForwardPagingDto(
          paging.before(),
          paging.after(),
          ENDPOINTS_DEFAULT_FIRST
        )
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
          .map(this.stringToCursorConverter::cursorToString),
        forwardPaging.after().map(this.stringToCursorConverter::cursorToString)
      )
      .map(this.endpointToOwnerEndpointDtoTransformer)
      .map(this.ownerEndpointDtoToOwnerEndpointEdgeDtoTransformer)
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
          .map(this.stringToCursorConverter::cursorToString),
        backwardPaging.after().map(this.stringToCursorConverter::cursorToString)
      )
      .map(this.endpointToOwnerEndpointDtoTransformer)
      .map(this.ownerEndpointDtoToOwnerEndpointEdgeDtoTransformer)
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
