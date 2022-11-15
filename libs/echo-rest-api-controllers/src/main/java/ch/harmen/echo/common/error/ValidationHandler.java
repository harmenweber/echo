package ch.harmen.echo.common.error;

import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class ValidationHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  ResponseEntity<List<String>> handleConstraintViolationException(
    ConstraintViolationException constraintViolationException
  ) {
    return ResponseEntity
      .badRequest()
      .body(
        constraintViolationException
          .getConstraintViolations()
          .stream()
          .map(ConstraintViolation::getMessage)
          .toList()
      );
  }
}
