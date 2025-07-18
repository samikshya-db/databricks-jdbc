package com.databricks.jdbc.telemetry;

import static com.databricks.jdbc.common.util.WildcardUtil.isNullOrEmpty;

import com.databricks.jdbc.api.internal.IDatabricksConnectionContext;
import com.databricks.jdbc.common.DatabricksClientConfiguratorManager;
import com.databricks.jdbc.common.safe.DatabricksDriverFeatureFlagsContextFactory;
import com.databricks.jdbc.common.util.DatabricksThreadContextHolder;
import com.databricks.jdbc.common.util.DriverUtil;
import com.databricks.jdbc.common.util.ProcessNameUtil;
import com.databricks.jdbc.common.util.StringUtil;
import com.databricks.jdbc.exception.DatabricksParsingException;
import com.databricks.jdbc.log.JdbcLogger;
import com.databricks.jdbc.log.JdbcLoggerFactory;
import com.databricks.jdbc.model.telemetry.*;
import com.databricks.jdbc.model.telemetry.latency.OperationType;
import com.databricks.jdbc.telemetry.latency.TelemetryCollector;
import com.databricks.sdk.core.DatabricksConfig;
import com.databricks.sdk.core.ProxyConfig;
import com.databricks.sdk.core.UserAgent;
import com.google.common.annotations.VisibleForTesting;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TelemetryHelper {
  private static final JdbcLogger LOGGER = JdbcLoggerFactory.getLogger(TelemetryHelper.class);
  // Cache to store unique DriverConnectionParameters for each connectionUuid
  private static final ConcurrentHashMap<String, DriverConnectionParameters>
      connectionParameterCache = new ConcurrentHashMap<>();

  @VisibleForTesting
  static final String TELEMETRY_FEATURE_FLAG_NAME =
      "databricks.partnerplatform.clientConfigsFeatureFlags.enableTelemetry";

  private static final DriverSystemConfiguration DRIVER_SYSTEM_CONFIGURATION =
      new DriverSystemConfiguration()
          .setCharSetEncoding(Charset.defaultCharset().displayName())
          .setDriverName(DriverUtil.getDriverName())
          .setDriverVersion(DriverUtil.getDriverVersion())
          .setLocaleName(
              System.getProperty("user.language") + '_' + System.getProperty("user.country"))
          .setRuntimeVendor(System.getProperty("java.vendor"))
          .setRuntimeVersion(System.getProperty("java.version"))
          .setRuntimeName(System.getProperty("java.vm.name"))
          .setOsArch(System.getProperty("os.arch"))
          .setOsVersion(System.getProperty("os.version"))
          .setOsName(System.getProperty("os.name"))
          .setProcessName(ProcessNameUtil.getProcessName())
          .setClientAppName(null);

  public static DriverSystemConfiguration getDriverSystemConfiguration() {
    return DRIVER_SYSTEM_CONFIGURATION;
  }

  public static void updateClientAppName(String clientAppName) {
    if (!isNullOrEmpty(clientAppName)) {
      DRIVER_SYSTEM_CONFIGURATION.setClientAppName(clientAppName);
    }
  }

  public static boolean isTelemetryAllowedForConnection(IDatabricksConnectionContext context) {
    if (context != null && context.forceEnableTelemetry()) {
      return true;
    }
    return context != null
        && context.isTelemetryEnabled()
        && DatabricksDriverFeatureFlagsContextFactory.getInstance(context)
            .isFeatureEnabled(TELEMETRY_FEATURE_FLAG_NAME);
  }

  public static void exportTelemetryLog(StatementTelemetryDetails telemetryDetails) {
    exportTelemetryEvent(
        DatabricksThreadContextHolder.getConnectionContext(), telemetryDetails, null, null);
  }

  private static void exportTelemetryEvent(
      IDatabricksConnectionContext connectionContext,
      StatementTelemetryDetails telemetryDetails,
      DriverErrorInfo errorInfo,
      Long chunkIndex) {
    if (connectionContext == null || telemetryDetails == null) {
      // This is when the context is not set or the telemetry details are not set.
      // In either of these scenarios, we don't export logs.
      return;
    }
    TelemetryEvent telemetryEvent =
        new TelemetryEvent()
            .setDriverConnectionParameters(getDriverConnectionParameter(connectionContext))
            .setSessionId(DatabricksThreadContextHolder.getSessionId())
            .setDriverErrorInfo(errorInfo) // This is only set for failure logs
            .setSqlStatementId(telemetryDetails.getStatementId())
            .setLatency(telemetryDetails.getOperationLatencyMillis());
    SqlExecutionEvent sqlExecutionEvent =
        new SqlExecutionEvent()
            .setChunkDetails(telemetryDetails.getChunkDetails())
            .setResultLatency(telemetryDetails.getResultLatency())
            .setOperationDetail(telemetryDetails.getOperationDetail())
            .setChunkId(chunkIndex); // This is only set for chunk download failure logs
    telemetryEvent.setSqlOperation(sqlExecutionEvent);

    TelemetryFrontendLog telemetryFrontendLog =
        new TelemetryFrontendLog()
            .setFrontendLogEventId(getEventUUID())
            .setContext(getLogContext())
            .setEntry(new FrontendLogEntry().setSqlDriverLog(telemetryEvent));
    TelemetryClientFactory.getInstance()
        .getTelemetryClient(connectionContext)
        .exportEvent(telemetryFrontendLog);
  }

  public static void exportFailureLog(
      IDatabricksConnectionContext connectionContext, String errorName, String errorMessage) {
    String statementId = DatabricksThreadContextHolder.getStatementId();
    exportFailureLog(
        connectionContext, errorName, errorMessage, statementId, /* chunkIndex */ null);
  }

  public static void exportFailureLog(
      IDatabricksConnectionContext connectionContext,
      String errorName,
      String errorMessage,
      String statementId,
      Long chunkIndex) {
    StatementTelemetryDetails telemetryDetails =
        TelemetryCollector.getInstance().getTelemetryDetails(statementId);
    DriverErrorInfo errorInfo =
        new DriverErrorInfo().setErrorName(errorName).setStackTrace(errorMessage);
    exportTelemetryEvent(connectionContext, telemetryDetails, errorInfo, chunkIndex);
  }

  private static DriverConnectionParameters getDriverConnectionParameter(
      IDatabricksConnectionContext connectionContext) {
    if (connectionContext == null) {
      return null;
    }
    return connectionParameterCache.computeIfAbsent(
        connectionContext.getConnectionUuid(),
        uuid -> buildDriverConnectionParameters(connectionContext));
  }

  private static DriverConnectionParameters buildDriverConnectionParameters(
      IDatabricksConnectionContext connectionContext) {
    String hostUrl;
    try {
      hostUrl = connectionContext.getHostUrl();
    } catch (DatabricksParsingException e) {
      hostUrl = "Error in parsing host url";
      // This would mean, telemetry data cannot be sent.
      return null;
    }
    DriverConnectionParameters connectionParameters =
        new DriverConnectionParameters()
            .setHostDetails(getHostDetails(hostUrl))
            .setUseProxy(connectionContext.getUseProxy())
            .setAuthMech(connectionContext.getAuthMech())
            .setAuthScope(connectionContext.getAuthScope())
            .setUseSystemProxy(connectionContext.getUseSystemProxy())
            .setUseCfProxy(connectionContext.getUseCloudFetchProxy())
            .setDriverAuthFlow(connectionContext.getAuthFlow())
            .setDiscoveryModeEnabled(connectionContext.isOAuthDiscoveryModeEnabled())
            .setDiscoveryUrl(connectionContext.getOAuthDiscoveryURL())
            .setIdentityFederationClientId(connectionContext.getIdentityFederationClientId())
            .setUseEmptyMetadata(connectionContext.getUseEmptyMetadata())
            .setSupportManyParameters(connectionContext.supportManyParameters())
            .setGoogleCredentialFilePath(connectionContext.getGoogleCredentials())
            .setGoogleServiceAccount(connectionContext.getGoogleServiceAccount())
            .setAllowedVolumeIngestionPaths(connectionContext.getVolumeOperationAllowedPaths())
            .setSocketTimeout(connectionContext.getSocketTimeout())
            .setStringColumnLength(connectionContext.getDefaultStringColumnLength())
            .setEnableComplexDatatypeSupport(connectionContext.isComplexDatatypeSupportEnabled())
            .setAzureWorkspaceResourceId(connectionContext.getAzureWorkspaceResourceId())
            .setAzureTenantId(connectionContext.getAzureTenantId())
            .setSslTrustStoreType(connectionContext.getSSLTrustStoreType())
            .setEnableArrow(connectionContext.shouldEnableArrow())
            .setEnableDirectResults(connectionContext.getDirectResultMode())
            .setCheckCertificateRevocation(connectionContext.checkCertificateRevocation())
            .setAcceptUndeterminedCertificateRevocation(
                connectionContext.acceptUndeterminedCertificateRevocation())
            .setDriverMode(connectionContext.getClientType().toString())
            .setAuthEndpoint(connectionContext.getAuthEndpoint())
            .setTokenEndpoint(connectionContext.getTokenEndpoint())
            .setNonProxyHosts(StringUtil.split(connectionContext.getNonProxyHosts()))
            .setHttpConnectionPoolSize(connectionContext.getHttpConnectionPoolSize())
            .setEnableSeaHybridResults(connectionContext.isSqlExecHybridResultsEnabled())
            .setAllowSelfSignedSupport(connectionContext.allowSelfSignedCerts())
            .setUseSystemTrustStore(connectionContext.useSystemTrustStore())
            .setRowsFetchedPerBlock(connectionContext.getRowsFetchedPerBlock())
            .setAsyncPollIntervalMillis(connectionContext.getAsyncExecPollInterval())
            .setEnableTokenCache(connectionContext.isTokenCacheEnabled())
            .setHttpPath(connectionContext.getHttpPath());
    if (connectionContext.useJWTAssertion()) {
      connectionParameters
          .setEnableJwtAssertion(true)
          .setJwtAlgorithm(connectionContext.getJWTAlgorithm())
          .setJwtKeyFile(connectionContext.getJWTKeyFile());
    }
    if (connectionContext.getUseCloudFetchProxy()) {
      connectionParameters.setCfProxyHostDetails(
          getHostDetails(
              connectionContext.getCloudFetchProxyHost(),
              connectionContext.getCloudFetchProxyPort(),
              connectionContext.getCloudFetchProxyAuthType()));
    }
    if (connectionContext.getUseProxy()) {
      HostDetails hostDetails =
          getHostDetails(
              connectionContext.getProxyHost(),
              connectionContext.getProxyPort(),
              connectionContext.getProxyAuthType());
      hostDetails.setNonProxyHosts(connectionContext.getNonProxyHosts());
      connectionParameters.setProxyHostDetails(hostDetails);
    } else if (connectionContext.getUseSystemProxy()) {
      String protocol = System.getProperty("https.proxyHost") != null ? "https" : "http";
      connectionParameters.setProxyHostDetails(
          getHostDetails(
              System.getProperty(protocol + ".proxyHost"),
              Integer.parseInt(System.getProperty(protocol + ".proxyPort")),
              connectionContext.getProxyAuthType()));
    }
    return connectionParameters;
  }

  private static String getEventUUID() {
    return UUID.randomUUID().toString();
  }

  private static FrontendLogContext getLogContext() {
    return new FrontendLogContext()
        .setClientContext(
            new TelemetryClientContext()
                .setTimestampMillis(Instant.now().toEpochMilli())
                .setUserAgent(UserAgent.asString()));
  }

  private static HostDetails getHostDetails(
      String host, int port, ProxyConfig.ProxyAuthType proxyAuthType) {
    return new HostDetails().setHostUrl(host).setPort(port).setProxyType(proxyAuthType);
  }

  private static HostDetails getHostDetails(String host) {
    return new HostDetails().setHostUrl(host);
  }

  public static DatabricksConfig getDatabricksConfigSafely(IDatabricksConnectionContext context) {
    try {
      return DatabricksClientConfiguratorManager.getInstance()
          .getConfiguratorOnlyIfExists(context)
          .getDatabricksConfig();
    } catch (Exception e) {
      String errorMessage =
          String.format(
              "Connection config is not available, using no-auth telemetry client. Error: %s; Context: %s",
              e.getMessage(), context);
      LOGGER.trace(errorMessage);
      return null;
    }
  }

  // Add mapping function for method name to OperationType
  public static OperationType mapMethodToOperationType(String methodName) {
    if (methodName == null) return OperationType.TYPE_UNSPECIFIED;
    switch (methodName) {
      case "createSession":
        return OperationType.CREATE_SESSION;
      case "executeStatement":
        return OperationType.EXECUTE_STATEMENT;
      case "executeStatementAsync":
        return OperationType.EXECUTE_STATEMENT_ASYNC;
      case "closeStatement":
        return OperationType.CLOSE_STATEMENT;
      case "cancelStatement":
        return OperationType.CANCEL_STATEMENT;
      case "deleteSession":
        return OperationType.DELETE_SESSION;
      case "listCrossReferences":
        return OperationType.LIST_CROSS_REFERENCES;
      case "listExportedKeys":
        return OperationType.LIST_EXPORTED_KEYS;
      case "listImportedKeys":
        return OperationType.LIST_IMPORTED_KEYS;
      case "listPrimaryKeys":
        return OperationType.LIST_PRIMARY_KEYS;
      case "listFunctions":
        return OperationType.LIST_FUNCTIONS;
      case "listColumns":
        return OperationType.LIST_COLUMNS;
      case "listTableTypes":
        return OperationType.LIST_TABLE_TYPES;
      case "listTables":
        return OperationType.LIST_TABLES;
      case "listSchemas":
        return OperationType.LIST_SCHEMAS;
      case "listCatalogs":
        return OperationType.LIST_CATALOGS;
      case "listTypeInfo":
        return OperationType.LIST_TYPE_INFO;
      default:
        return OperationType.TYPE_UNSPECIFIED;
    }
  }
}
