package ch.harmen.echo.endpoint;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class EndpointExceptionHandler {

  @ExceptionHandler(EndpointQuotaReachedException.class)
  ResponseEntity<List<String>> handleEndpointQuotaReachedException(
    EndpointQuotaReachedException exception
  ) {
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(List.of(exception.getMessage()));
  }

  @ExceptionHandler(EndpointNotFoundException.class)
  ResponseEntity<List<String>> handleEndpointNotFoundException(
    EndpointNotFoundException exception
  ) {
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(List.of(exception.getMessage()));
  }
}
