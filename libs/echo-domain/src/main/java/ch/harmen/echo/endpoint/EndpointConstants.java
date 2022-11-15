package ch.harmen.echo.endpoint;

public final class EndpointConstants {

  public static final int MAX_ENDPOINTS_PER_OWNER = 10;
  public static final String ID_PATH_VARIABLE = "id";
  public static final String ENDPOINTS_PATH = "/endpoints";
  public static final String ENDPOINT_PATH =
    ENDPOINTS_PATH + "/{" + ID_PATH_VARIABLE + "}";
}
