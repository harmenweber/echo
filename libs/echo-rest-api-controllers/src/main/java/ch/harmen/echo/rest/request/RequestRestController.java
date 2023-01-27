package ch.harmen.echo.rest.request;

import ch.harmen.echo.endpoint.EndpointService;
import ch.harmen.echo.request.ApiKeyIncorrectException;
import ch.harmen.echo.request.RequestConstants;
import ch.harmen.echo.request.RequestService;
import ch.harmen.echo.user.CurrentUserContextSupplier;
import java.util.Objects;
import javax.validation.constraints.Min;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping(path = RequestConstants.REQUESTS_PATH)
class RequestRestController {

  private final RequestService requestService;
  private final EndpointService endpointService;
  private final CurrentUserContextSupplier currentUserContextSupplier;
  private final RequestEntityToRequestTransformer requestEntityToRequestTransformer;
  private final RequestToRequestDtoTransformer requestToRequestDtoTransformer;
  private final RequestToCreateRequestResultDtoTransformer requestToCreateRequestResultDtoTransformer;

  RequestRestController(
    RequestService requestService,
    EndpointService endpointService,
    CurrentUserContextSupplier currentUserContextSupplier,
    RequestEntityToRequestTransformer requestEntityToRequestTransformer,
    RequestToRequestDtoTransformer requestToRequestDtoTransformer,
    RequestToCreateRequestResultDtoTransformer requestToCreateRequestResultDtoTransformer
  ) {
    this.requestService = Objects.requireNonNull(requestService);
    this.endpointService = Objects.requireNonNull(endpointService);
    this.currentUserContextSupplier =
      Objects.requireNonNull(currentUserContextSupplier);
    this.requestEntityToRequestTransformer =
      Objects.requireNonNull(requestEntityToRequestTransformer);
    this.requestToRequestDtoTransformer =
      Objects.requireNonNull(requestToRequestDtoTransformer);
    this.requestToCreateRequestResultDtoTransformer =
      Objects.requireNonNull(requestToCreateRequestResultDtoTransformer);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Mono<CreateRequestResultDto> create(
    @PathVariable(
      RequestConstants.ENDPOINT_ID_PATH_VARIABLE
    ) final String endpointId,
    @RequestHeader(
      name = RequestConstants.API_KEY_HEADER_NAME,
      required = false
    ) final String apiKey,
    final RequestEntity<byte[]> request
  ) {
    return this.endpointService.getById(endpointId)
      .filter(endpoint -> endpoint.apiKey().equals(apiKey))
      .switchIfEmpty(
        Mono.error(new ApiKeyIncorrectException(endpointId, apiKey))
      )
      .map(endpoint ->
        this.requestEntityToRequestTransformer.apply(endpoint.id(), request)
      )
      .flatMap(this.requestService::create)
      .map(this.requestToCreateRequestResultDtoTransformer);
  }

  @GetMapping(path = "/{id}")
  Mono<ResponseEntity<RequestDto>> get(
    @PathVariable(
      RequestConstants.ENDPOINT_ID_PATH_VARIABLE
    ) final String endpointId,
    @PathVariable("id") final String id
  ) {
    return this.endpointService.getByOwnerAndId(
        this.currentUserContextSupplier.get().id(),
        endpointId
      )
      .flatMap(endpoint ->
        this.requestService.getByEndpointIdAndId(endpoint.id(), id)
      )
      .map(this.requestToRequestDtoTransformer)
      .map(ResponseEntity::ok);
  }

  @DeleteMapping(path = "/{id}")
  Mono<ResponseEntity<Void>> delete(
    @PathVariable(
      RequestConstants.ENDPOINT_ID_PATH_VARIABLE
    ) final String endpointId,
    @PathVariable("id") final String id
  ) {
    return this.endpointService.getByOwnerAndId(
        this.currentUserContextSupplier.get().id(),
        endpointId
      )
      .flatMap(endpoint ->
        this.requestService.getByEndpointIdAndId(endpoint.id(), id)
      )
      .flatMap(this.requestService::delete)
      .map(endpoint -> ResponseEntity.ok().build());
  }

  @GetMapping
  Flux<RequestDto> get(
    @PathVariable(
      RequestConstants.ENDPOINT_ID_PATH_VARIABLE
    ) final String endpointId,
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
    return this.endpointService.getByOwnerAndId(
        this.currentUserContextSupplier.get().id(),
        endpointId
      )
      .flatMapMany(endpoint ->
        this.requestService.findByEndpointId(endpoint.id(), page, pageSize)
      )
      .map(this.requestToRequestDtoTransformer);
  }
}
