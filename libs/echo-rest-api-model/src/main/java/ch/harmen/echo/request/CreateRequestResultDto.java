package ch.harmen.echo.request;

public record CreateRequestResultDto(String id) {
  public CreateRequestResultDto(final CreateRequestResultDto original) {
    this(original.id);
  }
}
