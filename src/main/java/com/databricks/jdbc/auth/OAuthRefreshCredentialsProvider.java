package com.databricks.jdbc.auth;

import static com.databricks.jdbc.auth.AuthConstants.GRANT_TYPE_KEY;
import static com.databricks.jdbc.auth.AuthConstants.GRANT_TYPE_REFRESH_TOKEN_KEY;

import com.databricks.jdbc.api.internal.IDatabricksConnectionContext;
import com.databricks.jdbc.common.DatabricksJdbcConstants;
import com.databricks.jdbc.common.util.DatabricksAuthUtil;
import com.databricks.jdbc.exception.DatabricksParsingException;
import com.databricks.jdbc.log.JdbcLogger;
import com.databricks.jdbc.log.JdbcLoggerFactory;
import com.databricks.sdk.core.CredentialsProvider;
import com.databricks.sdk.core.DatabricksConfig;
import com.databricks.sdk.core.DatabricksException;
import com.databricks.sdk.core.HeaderFactory;
import com.databricks.sdk.core.http.HttpClient;
import com.databricks.sdk.core.oauth.AuthParameterPosition;
import com.databricks.sdk.core.oauth.RefreshableTokenSource;
import com.databricks.sdk.core.oauth.Token;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHeaders;

public class OAuthRefreshCredentialsProvider extends RefreshableTokenSource
    implements CredentialsProvider {

  private static final JdbcLogger LOGGER =
      JdbcLoggerFactory.getLogger(OAuthRefreshCredentialsProvider.class);
  private HttpClient hc;
  private final String tokenEndpoint;
  private final String clientId;
  private final String clientSecret;

  public OAuthRefreshCredentialsProvider(
      IDatabricksConnectionContext context, DatabricksConfig databricksConfig) {
    this.tokenEndpoint = DatabricksAuthUtil.getTokenEndpoint(databricksConfig, context);
    try {
      this.clientId = context.getClientId();
    } catch (DatabricksParsingException e) {
      String exceptionMessage = "Failed to parse client id";
      LOGGER.error(exceptionMessage);
      throw new DatabricksException(exceptionMessage, e);
    }
    this.clientSecret = context.getClientSecret();
    // Create an expired dummy token object with the refresh token to use
    this.token =
        new Token(
            DatabricksJdbcConstants.EMPTY_STRING,
            DatabricksJdbcConstants.EMPTY_STRING,
            context.getOAuthRefreshToken(),
            LocalDateTime.now().minusMinutes(1));
  }

  @Override
  public String authType() {
    return "oauth-refresh";
  }

  @Override
  public HeaderFactory configure(DatabricksConfig databricksConfig) {
    if (this.hc == null) {
      this.hc = databricksConfig.getHttpClient();
    }
    return () -> {
      Map<String, String> headers = new HashMap<>();
      // An example header looks like: "Authorization: Bearer <access-token>"
      headers.put(
          HttpHeaders.AUTHORIZATION, getToken().getTokenType() + " " + getToken().getAccessToken());
      return headers;
    };
  }

  @Override
  protected Token refresh() {
    if (this.token == null) {
      String exceptionMessage = "oauth2: token is not set";
      LOGGER.error(exceptionMessage);
      throw new DatabricksException(exceptionMessage);
    }
    String refreshToken = this.token.getRefreshToken();
    if (refreshToken == null) {
      String exceptionMessage = "oauth2: token expired and refresh token is not set";
      LOGGER.error(exceptionMessage);
      throw new DatabricksException(exceptionMessage);
    }

    Map<String, String> params = new HashMap<>();
    params.put(GRANT_TYPE_KEY, GRANT_TYPE_REFRESH_TOKEN_KEY);
    params.put(GRANT_TYPE_REFRESH_TOKEN_KEY, refreshToken);
    Map<String, String> headers = new HashMap<>();
    return retrieveToken(
        hc, clientId, clientSecret, tokenEndpoint, params, headers, AuthParameterPosition.BODY);
  }
}
