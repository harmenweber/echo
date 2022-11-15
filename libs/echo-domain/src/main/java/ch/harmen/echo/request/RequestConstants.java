package ch.harmen.echo.request;

import ch.harmen.echo.endpoint.EndpointConstants;

public final class RequestConstants {

  public static final int MAX_REQUESTS_PER_ENDPOINT = 50;
  public static final String ID_PATH_VARIABLE = "id";
  public static final String ENDPOINT_ID_PATH_VARIABLE = "endpointId";
  public static final String REQUESTS_PATH =
    EndpointConstants.ENDPOINTS_PATH +
    "/{" +
    ENDPOINT_ID_PATH_VARIABLE +
    "}/requests";
  public static final String REQUEST_PATH =
    RequestConstants.REQUESTS_PATH +
    "/{" +
    RequestConstants.ID_PATH_VARIABLE +
    "}";
}
