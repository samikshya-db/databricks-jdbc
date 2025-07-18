package com.databricks.jdbc.telemetry;

import static com.databricks.jdbc.telemetry.TelemetryHelper.isTelemetryAllowedForConnection;

import com.databricks.jdbc.api.internal.IDatabricksConnectionContext;
import com.databricks.jdbc.log.JdbcLogger;
import com.databricks.jdbc.log.JdbcLoggerFactory;
import com.databricks.sdk.core.DatabricksConfig;
import com.google.common.annotations.VisibleForTesting;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelemetryClientFactory {

  private static final JdbcLogger LOGGER =
      JdbcLoggerFactory.getLogger(TelemetryClientFactory.class);

  private static final TelemetryClientFactory INSTANCE = new TelemetryClientFactory();

  @VisibleForTesting
  final LinkedHashMap<String, TelemetryClient> telemetryClients = new LinkedHashMap<>();

  @VisibleForTesting
  final LinkedHashMap<String, TelemetryClient> noauthTelemetryClients = new LinkedHashMap<>();

  private final ExecutorService telemetryExecutorService;

  private TelemetryClientFactory() {
    telemetryExecutorService = Executors.newFixedThreadPool(10);
  }

  public static TelemetryClientFactory getInstance() {
    return INSTANCE;
  }

  public ITelemetryClient getTelemetryClient(IDatabricksConnectionContext connectionContext) {
    if (!isTelemetryAllowedForConnection(connectionContext)) {
      return NoopTelemetryClient.getInstance();
    }
    DatabricksConfig databricksConfig =
        TelemetryHelper.getDatabricksConfigSafely(connectionContext);
    if (databricksConfig != null) {
      return telemetryClients.computeIfAbsent(
          connectionContext.getConnectionUuid(),
          k ->
              new TelemetryClient(
                  connectionContext, getTelemetryExecutorService(), databricksConfig));
    }
    // Use noauth telemetry client if connection creation failed.
    return noauthTelemetryClients.computeIfAbsent(
        connectionContext.getConnectionUuid(),
        k -> new TelemetryClient(connectionContext, getTelemetryExecutorService()));
  }

  public void closeTelemetryClient(IDatabricksConnectionContext connectionContext) {
    closeTelemetryClient(
        telemetryClients.remove(connectionContext.getConnectionUuid()), "telemetry client");
    closeTelemetryClient(
        noauthTelemetryClients.remove(connectionContext.getConnectionUuid()),
        "unauthenticated telemetry client");
  }

  public ExecutorService getTelemetryExecutorService() {
    return telemetryExecutorService;
  }

  @VisibleForTesting
  public void reset() {
    // Close all existing clients
    telemetryClients.values().forEach(TelemetryClient::close);
    noauthTelemetryClients.values().forEach(TelemetryClient::close);

    // Clear the maps
    telemetryClients.clear();
    noauthTelemetryClients.clear();
  }

  private void closeTelemetryClient(ITelemetryClient client, String clientType) {
    if (client != null) {
      try {
        client.close();
      } catch (Exception e) {
        LOGGER.debug("Caught error while closing {}. Error: {}", clientType, e);
      }
    }
  }
}
