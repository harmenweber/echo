package ch.harmen.echo.endpoint;

import ch.harmen.echo.user.CurrentUserContextSupplier;
import java.util.Objects;
import javax.validation.constraints.Min;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping(path = EndpointRestController.ENDPOINTS_PATH)
class EndpointRestController {

  static final String ENDPOINTS_PATH = "/endpoints";

  private final EndpointService endpointService;
  private final EndpointToEndpointDtoTransformer endpointToEndpointDtoTransformer;
  private final CurrentUserContextSupplier currentUserContextSupplier;

  EndpointRestController(
    EndpointService endpointService,
    EndpointToEndpointDtoTransformer endpointToEndpointDtoTransformer,
    CurrentUserContextSupplier currentUserContextSupplier
  ) {
    this.endpointService = Objects.requireNonNull(endpointService);
    this.endpointToEndpointDtoTransformer =
      Objects.requireNonNull(endpointToEndpointDtoTransformer);
    this.currentUserContextSupplier =
      Objects.requireNonNull(currentUserContextSupplier);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Mono<EndpointDto> create() {
    return this.endpointService.create()
      .map(this.endpointToEndpointDtoTransformer);
  }

  @GetMapping(path = "/{id}")
  Mono<ResponseEntity<EndpointDto>> get(@PathVariable("id") final String id) {
    return this.endpointService.findByOwnerAndId(
        this.currentUserContextSupplier.get().id(),
        id
      )
      .map(this.endpointToEndpointDtoTransformer)
      .map(ResponseEntity::ok)
      .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @DeleteMapping(path = "/{id}")
  Mono<ResponseEntity<Void>> delete(@PathVariable("id") final String id) {
    return this.endpointService.findByOwnerAndId(
        this.currentUserContextSupplier.get().id(),
        id
      )
      .flatMap(endpoint ->
        this.endpointService.delete(endpoint).thenReturn(endpoint)
      )
      .map(endpoint -> ResponseEntity.ok().<Void>build())
      .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping
  Flux<EndpointDto> get(
    @Min(
      value = 0,
      message = "{ch.harmen.echo.endpoints.get.page.min.requirement}"
    ) @RequestParam(defaultValue = "0") final int page,
    @Range(
      min = 1,
      max = 5000,
      message = "{ch.harmen.echo.endpoints.get.pageSize.range.requirement}"
    ) @RequestParam(defaultValue = "100") final int pageSize
  ) {
    return this.endpointService.findByOwner(
        this.currentUserContextSupplier.get().id(),
        page,
        pageSize
      )
      .map(this.endpointToEndpointDtoTransformer);
  }
}
