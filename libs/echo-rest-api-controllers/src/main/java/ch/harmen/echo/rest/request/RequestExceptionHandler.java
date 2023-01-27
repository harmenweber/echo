package ch.harmen.echo.rest.request;

import ch.harmen.echo.request.ApiKeyIncorrectException;
import ch.harmen.echo.request.RequestNotFoundException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class RequestExceptionHandler {

  @ExceptionHandler(RequestNotFoundException.class)
  ResponseEntity<List<String>> handleRequestNotFoundException(
    RequestNotFoundException exception
  ) {
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(List.of(exception.getMessage()));
  }

  @ExceptionHandler(ApiKeyIncorrectException.class)
  ResponseEntity<List<String>> handleApiKeyIncorrectException(
    ApiKeyIncorrectException exception
  ) {
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(List.of(exception.getMessage()));
  }
}
