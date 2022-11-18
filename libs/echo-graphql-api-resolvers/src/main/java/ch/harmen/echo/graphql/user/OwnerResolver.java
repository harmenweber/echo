package ch.harmen.echo.graphql.user;

import ch.harmen.echo.endpoint.EndpointConstants;
import ch.harmen.echo.endpoint.EndpointService;
import ch.harmen.echo.graphql.common.PageInfoDtoFactory;
import ch.harmen.echo.graphql.common.PagingDto;
import ch.harmen.echo.graphql.endpoint.EndpointDtoToEndpointEdgeDtoTransformer;
import ch.harmen.echo.graphql.endpoint.EndpointToEndpointDtoTransformer;
import ch.harmen.echo.user.CurrentUserContextSupplier;
import java.util.Collections;
import java.util.Objects;
import org.springframework.graphql.data.method.annotation.Arguments;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@SchemaMapping(typeName = "UserDto")
class OwnerResolver {

  private final CurrentUserContextSupplier currentUserContextSupplier;
  private final EndpointService endpointService;
  private final EndpointToEndpointDtoTransformer endpointToEndpointDtoTransformer;
  private final EndpointDtoToEndpointEdgeDtoTransformer endpointDtoToEndpointEdgeDtoTransformer;
  private final PageInfoDtoFactory pageInfoDtoFactory;

  OwnerResolver(
    CurrentUserContextSupplier currentUserContextSupplier,
    EndpointService endpointService,
    EndpointToEndpointDtoTransformer endpointToEndpointDtoTransformer,
    EndpointDtoToEndpointEdgeDtoTransformer endpointDtoToEndpointEdgeDtoTransformer,
    PageInfoDtoFactory pageInfoDtoFactory
  ) {
    this.currentUserContextSupplier =
      Objects.requireNonNull(currentUserContextSupplier);
    this.endpointService = Objects.requireNonNull(endpointService);
    this.endpointToEndpointDtoTransformer =
      Objects.requireNonNull(endpointToEndpointDtoTransformer);
    this.endpointDtoToEndpointEdgeDtoTransformer =
      Objects.requireNonNull(endpointDtoToEndpointEdgeDtoTransformer);
    this.pageInfoDtoFactory = Objects.requireNonNull(pageInfoDtoFactory);
  }

  @QueryMapping
  Mono<UserDto> owner() {
    return Mono
      .just(this.currentUserContextSupplier.get().id())
      .map(id -> new UserDto(id, Collections.emptyList()));
  }

  @SchemaMapping
  Mono<OwnerEndpointsConnectionDto> endpoints(
    final UserDto owner,
    @Arguments final PagingDto paging
  ) {
    // TODO apply paging
    return this.endpointService.findByOwner(
        owner.id(),
        0,
        EndpointConstants.MAX_ENDPOINTS_PER_OWNER
      )
      .map(this.endpointToEndpointDtoTransformer)
      .map(this.endpointDtoToEndpointEdgeDtoTransformer)
      .collectList()
      .map(edges ->
        new OwnerEndpointsConnectionDto(
          edges,
          this.pageInfoDtoFactory.create(edges, paging)
        )
      );
  }
}
