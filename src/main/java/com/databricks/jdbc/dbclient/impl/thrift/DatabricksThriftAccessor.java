package com.databricks.jdbc.dbclient.impl.thrift;

import static com.databricks.jdbc.common.EnvironmentVariables.*;
import static com.databricks.jdbc.common.util.DatabricksThriftUtil.*;

import com.databricks.jdbc.api.impl.*;
import com.databricks.jdbc.api.internal.IDatabricksConnectionContext;
import com.databricks.jdbc.api.internal.IDatabricksSession;
import com.databricks.jdbc.api.internal.IDatabricksStatementInternal;
import com.databricks.jdbc.common.DatabricksClientConfiguratorManager;
import com.databricks.jdbc.common.StatementType;
import com.databricks.jdbc.common.util.DatabricksThreadContextHolder;
import com.databricks.jdbc.common.util.DriverUtil;
import com.databricks.jdbc.common.util.ProtocolFeatureUtil;
import com.databricks.jdbc.dbclient.impl.common.StatementId;
import com.databricks.jdbc.dbclient.impl.common.TimeoutHandler;
import com.databricks.jdbc.dbclient.impl.http.DatabricksHttpClientFactory;
import com.databricks.jdbc.exception.DatabricksHttpException;
import com.databricks.jdbc.exception.DatabricksParsingException;
import com.databricks.jdbc.exception.DatabricksSQLException;
import com.databricks.jdbc.exception.DatabricksSQLFeatureNotSupportedException;
import com.databricks.jdbc.log.JdbcLogger;
import com.databricks.jdbc.log.JdbcLoggerFactory;
import com.databricks.jdbc.model.client.thrift.generated.*;
import com.databricks.jdbc.model.core.StatementStatus;
import com.databricks.jdbc.model.telemetry.enums.DatabricksDriverErrorCode;
import com.databricks.jdbc.telemetry.latency.TelemetryCollector;
import com.databricks.sdk.core.DatabricksConfig;
import com.databricks.sdk.service.sql.StatementState;
import com.google.common.annotations.VisibleForTesting;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.protocol.TBinaryProtocol;

final class DatabricksThriftAccessor {

  private static final JdbcLogger LOGGER =
      JdbcLoggerFactory.getLogger(DatabricksThriftAccessor.class);
  private static final TSparkGetDirectResults DEFAULT_DIRECT_RESULTS =
      new TSparkGetDirectResults()
          .setMaxRows(DEFAULT_ROW_LIMIT_PER_BLOCK)
          .setMaxBytes(DEFAULT_BYTE_LIMIT);
  private static final short directResultsFieldId =
      TExecuteStatementResp._Fields.DIRECT_RESULTS.getThriftFieldId();
  private static final short operationHandleFieldId =
      TExecuteStatementResp._Fields.OPERATION_HANDLE.getThriftFieldId();
  private static final short statusFieldId =
      TExecuteStatementResp._Fields.STATUS.getThriftFieldId();
  private final ThreadLocal<TCLIService.Client> thriftClient;
  private final DatabricksConfig databricksConfig;
  private final boolean enableDirectResults;
  private final int asyncPollIntervalMillis;
  private final int maxRowsPerBlock;
  private final String connectionUuid;
  private TProtocolVersion serverProtocolVersion = JDBC_THRIFT_VERSION;

  DatabricksThriftAccessor(IDatabricksConnectionContext connectionContext)
      throws DatabricksParsingException {
    this.enableDirectResults = connectionContext.getDirectResultMode();
    this.databricksConfig =
        DatabricksClientConfiguratorManager.getInstance()
            .getConfigurator(connectionContext)
            .getDatabricksConfig();
    String endPointUrl = connectionContext.getEndpointURL();
    this.asyncPollIntervalMillis = connectionContext.getAsyncExecPollInterval();
    this.maxRowsPerBlock = connectionContext.getRowsFetchedPerBlock();
    this.connectionUuid = connectionContext.getConnectionUuid();

    if (!DriverUtil.isRunningAgainstFake()) {
      // Create a new thrift client for each thread as client state is not thread safe. Note that
      // the underlying protocol uses the same http client which is thread safe
      this.thriftClient =
          ThreadLocal.withInitial(
              () -> createThriftClient(endPointUrl, databricksConfig, connectionContext));
    } else {
      TCLIService.Client client =
          createThriftClient(endPointUrl, databricksConfig, connectionContext);
      this.thriftClient = ThreadLocal.withInitial(() -> client);
    }
  }

  @VisibleForTesting
  DatabricksThriftAccessor(
      TCLIService.Client client, IDatabricksConnectionContext connectionContext) {
    this.databricksConfig = null;
    this.thriftClient = ThreadLocal.withInitial(() -> client);
    this.enableDirectResults = connectionContext.getDirectResultMode();
    this.asyncPollIntervalMillis = connectionContext.getAsyncExecPollInterval();
    this.maxRowsPerBlock = connectionContext.getRowsFetchedPerBlock();
    this.connectionUuid = connectionContext.getConnectionUuid();
  }

  @SuppressWarnings("rawtypes")
  TBase getThriftResponse(TBase request) throws DatabricksSQLException {
    LOGGER.debug("Fetching thrift response for request {}", request.toString());
    try {
      if (request instanceof TOpenSessionReq) {
        return getThriftClient().OpenSession((TOpenSessionReq) request);
      } else if (request instanceof TCloseSessionReq) {
        return getThriftClient().CloseSession((TCloseSessionReq) request);
      } else if (request instanceof TGetFunctionsReq) {
        return listFunctions((TGetFunctionsReq) request);
      } else if (request instanceof TGetPrimaryKeysReq) {
        return listPrimaryKeys((TGetPrimaryKeysReq) request);
      } else if (request instanceof TGetCrossReferenceReq) {
        return listCrossReferences((TGetCrossReferenceReq) request);
      } else if (request instanceof TGetCatalogsReq) {
        return getCatalogs((TGetCatalogsReq) request);
      } else if (request instanceof TGetTablesReq) {
        return getTables((TGetTablesReq) request);
      } else if (request instanceof TGetTableTypesReq) {
        return getTableTypes((TGetTableTypesReq) request);
      } else if (request instanceof TGetSchemasReq) {
        return listSchemas((TGetSchemasReq) request);
      } else if (request instanceof TGetTypeInfoReq) {
        return getTypeInfo((TGetTypeInfoReq) request);
      } else if (request instanceof TGetColumnsReq) {
        return listColumns((TGetColumnsReq) request);
      }
      String errorMessage =
          String.format("No implementation for fetching thrift response for Request {%s}", request);
      LOGGER.error(errorMessage);
      throw new DatabricksSQLFeatureNotSupportedException(errorMessage);
    } catch (TException | SQLException e) {
      Throwable cause = e;
      while (cause != null) {
        if (cause instanceof HttpException) {
          throw new DatabricksHttpException(
              cause.getMessage(), cause, DatabricksDriverErrorCode.INVALID_STATE);
        }
        cause = cause.getCause();
      }
      String errorMessage =
          String.format(
              "Error while receiving response from Thrift server. Request {%s}, Error {%s}",
              request, e.getMessage());
      LOGGER.error(e, errorMessage);
      if (e instanceof SQLException) {
        throw new DatabricksSQLException(errorMessage, e, ((SQLException) e).getSQLState());
      } else {
        throw new DatabricksSQLException(errorMessage, e, DatabricksDriverErrorCode.INVALID_STATE);
      }
    }
  }

  TFetchResultsResp getResultSetResp(TOperationHandle operationHandle, String context)
      throws DatabricksHttpException {
    return getResultSetResp(
        new TStatus().setStatusCode(TStatusCode.SUCCESS_STATUS),
        operationHandle,
        context,
        maxRowsPerBlock,
        false);
  }

  TCancelOperationResp cancelOperation(TCancelOperationReq req) throws DatabricksHttpException {
    try {
      return getThriftClient().CancelOperation(req);
    } catch (TException e) {
      String errorMessage =
          String.format(
              "Error while canceling operation from Thrift server. Request {%s}, Error {%s}",
              req.toString(), e.getMessage());
      LOGGER.error(e, errorMessage);
      throw new DatabricksHttpException(errorMessage, e, DatabricksDriverErrorCode.INVALID_STATE);
    }
  }

  TCloseOperationResp closeOperation(TCloseOperationReq req) throws DatabricksHttpException {
    try {
      return getThriftClient().CloseOperation(req);
    } catch (TException e) {
      String errorMessage =
          String.format(
              "Error while closing operation from Thrift server. Request {%s}, Error {%s}",
              req.toString(), e.getMessage());
      LOGGER.error(e, errorMessage);
      throw new DatabricksHttpException(errorMessage, e, DatabricksDriverErrorCode.INVALID_STATE);
    }
  }

  TFetchResultsResp getMoreResults(IDatabricksStatementInternal parentStatement)
      throws DatabricksSQLException {
    String context =
        String.format(
            "Fetching more results as it has more rows %s",
            parentStatement.getStatementId().toSQLExecStatementId());
    return getResultSetResp(
        new TStatus().setStatusCode(TStatusCode.SUCCESS_STATUS),
        getOperationHandle(parentStatement.getStatementId()),
        context,
        maxRowsPerBlock,
        true);
  }

  DatabricksResultSet execute(
      TExecuteStatementReq request,
      IDatabricksStatementInternal parentStatement,
      IDatabricksSession session,
      StatementType statementType)
      throws SQLException {

    try {
      // Set direct result configuration
      if (enableDirectResults) {
        // if getDirectResults.maxRows > 0, the server will immediately call FetchResults. Fetch
        // initial rows limited by maxRows.
        // if = 0, server does not call FetchResults.
        TSparkGetDirectResults directResults =
            new TSparkGetDirectResults()
                .setMaxBytes(DEFAULT_BYTE_LIMIT)
                .setMaxRows(maxRowsPerBlock);
        request.setGetDirectResults(directResults);
      }
      TExecuteStatementResp response;
      TFetchResultsResp resultSet;
      response = getThriftClient().ExecuteStatement(request);
      checkResponseForErrors(response);

      StatementId statementId = new StatementId(response.getOperationHandle().operationId);
      DatabricksThreadContextHolder.setStatementId(statementId);
      if (parentStatement != null) {
        parentStatement.setStatementId(statementId);
      }

      // Get the operation status from direct results if present
      String sessionDebugInfo =
          String.format(
              "Session [%s] with (%s)", session.getSessionId(), session.getComputeResource());

      TGetOperationStatusResp statusResp =
          pollTillOperationFinished(
              response, parentStatement, session, statementId, sessionDebugInfo);
      if (hasResultDataInDirectResults(response)) {
        // The first response has result data
        // There is no polling in this case as status was already finished
        resultSet = response.getDirectResults().getResultSet();
        resultSet.setResultSetMetadata(response.getDirectResults().getResultSetMetadata());
      } else {
        // Fetch the result data after polling
        long fetchStartTime = System.nanoTime();
        resultSet =
            getResultSetResp(
                response.getStatus(),
                response.getOperationHandle(),
                response.toString(),
                maxRowsPerBlock,
                true);
        long fetchEndTime = System.nanoTime();
        long fetchLatencyNanos = fetchEndTime - fetchStartTime;
        long fetchLatencyMillis = fetchLatencyNanos / 1_000_000;
        LOGGER.debug(
            String.format(
                "Connection [%s] Statement [%s] Session [%s] Thrift fetch latency: %dms",
                connectionUuid, statementId, sessionDebugInfo, fetchLatencyMillis));
      }
      return new DatabricksResultSet(
          getStatementStatus(statusResp),
          statementId,
          resultSet,
          statementType,
          parentStatement,
          session);
    } catch (TException e) {
      String errorMessage =
          String.format(
              "Error while receiving response from Thrift server. Request {%s}, Error {%s}",
              request, e.getMessage());
      LOGGER.error(e, errorMessage);
      throw new DatabricksHttpException(errorMessage, e, DatabricksDriverErrorCode.INVALID_STATE);
    }
  }

  private TGetOperationStatusResp pollTillOperationFinished(
      TExecuteStatementResp response,
      IDatabricksStatementInternal parentStatement,
      IDatabricksSession session,
      StatementId statementId,
      String sessionDebugInfo)
      throws SQLException, TException {
    int timeoutInSeconds =
        (parentStatement == null) ? 0 : parentStatement.getStatement().getQueryTimeout();

    TGetOperationStatusResp statusResp = null;
    if (response.isSetDirectResults()) {
      checkDirectResultsForErrorStatus(response.getDirectResults(), response.toString());
      statusResp = response.getDirectResults().getOperationStatus();
      checkOperationStatusForErrors(statusResp);
    }

    TimeoutHandler timeoutHandler = getTimeoutHandler(response, timeoutInSeconds);

    // Polling until query operation state is finished
    long pollingStartTime = System.nanoTime();
    TGetOperationStatusReq statusReq =
        new TGetOperationStatusReq()
            .setOperationHandle(response.getOperationHandle())
            .setGetProgressUpdate(false);
    while (shouldContinuePolling(statusResp)) {
      // Check for timeout before continuing
      timeoutHandler.checkTimeout();

      // Polling for operation status
      statusResp = getOperationStatus(statusReq, statementId);
      checkOperationStatusForErrors(statusResp);
      // Save some time if sleep isn't required by breaking.
      if (!shouldContinuePolling(statusResp)) {
        break;
      }
      try {
        TimeUnit.MILLISECONDS.sleep(asyncPollIntervalMillis);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // Restore interrupt flag
        cancelOperation(
            new TCancelOperationReq().setOperationHandle(response.getOperationHandle()));
        throw new DatabricksSQLException(
            "Query execution interrupted", e, DatabricksDriverErrorCode.THREAD_INTERRUPTED_ERROR);
      }
    }
    long pollingEndTime = System.nanoTime();
    long pollingLatencyNanos = pollingEndTime - pollingStartTime;
    long pollingLatencyMillis = pollingLatencyNanos / 1_000_000;
    LOGGER.debug(
        String.format(
            "Connection [%s] Statement [%s] Session [%s] Thrift polling latency: %dms",
            connectionUuid, statementId, sessionDebugInfo, pollingLatencyMillis));
    return statusResp;
  }

  DatabricksResultSet executeAsync(
      TExecuteStatementReq request,
      IDatabricksStatementInternal parentStatement,
      IDatabricksSession session,
      StatementType statementType)
      throws SQLException {

    TExecuteStatementResp response;
    try {
      response = getThriftClient().ExecuteStatement(request);
      if (Arrays.asList(TStatusCode.ERROR_STATUS, TStatusCode.INVALID_HANDLE_STATUS)
          .contains(response.status.statusCode)) {
        LOGGER.error(
            "Received error response {} from Thrift Server for request {}",
            response,
            request.toString());
        throw new DatabricksSQLException(response.status.errorMessage, response.status.sqlState);
      }
    } catch (DatabricksSQLException | TException e) {

      String errorMessage =
          String.format(
              "Error while receiving response from Thrift server. Request {%s}, Error {%s}",
              request.toString(), e.getMessage());
      LOGGER.error(e, errorMessage);
      if (e instanceof DatabricksSQLException) {
        throw new DatabricksHttpException(errorMessage, ((DatabricksSQLException) e).getSQLState());
      } else {
        throw new DatabricksHttpException(errorMessage, e, DatabricksDriverErrorCode.INVALID_STATE);
      }
    }
    StatementId statementId = new StatementId(response.getOperationHandle().operationId);
    DatabricksThreadContextHolder.setStatementId(statementId);
    if (parentStatement != null) {
      parentStatement.setStatementId(statementId);
    }
    StatementStatus statementStatus = getAsyncStatus(response.getStatus());

    return new DatabricksResultSet(
        statementStatus, statementId, null, statementType, parentStatement, session);
  }

  DatabricksResultSet getStatementResult(
      TOperationHandle operationHandle,
      IDatabricksStatementInternal parentStatement,
      IDatabricksSession session)
      throws SQLException {
    LOGGER.debug("Operation handle {}", operationHandle);

    long getStatementResultStartTime = System.nanoTime();
    StatementId statementId = new StatementId(operationHandle.getOperationId());
    String sessionInfo = session.getSessionId() + " (" + session.getComputeResource() + ")";

    TGetOperationStatusReq request =
        new TGetOperationStatusReq()
            .setOperationHandle(operationHandle)
            .setGetProgressUpdate(false);
    TGetOperationStatusResp response;
    TFetchResultsResp resultSet = null;
    try {
      response = getOperationStatus(request, statementId);
      TOperationState operationState = response.getOperationState();
      if (operationState == TOperationState.FINISHED_STATE) {
        long fetchStartTime = System.nanoTime();
        resultSet =
            getResultSetResp(response.getStatus(), operationHandle, response.toString(), -1, true);
        long fetchEndTime = System.nanoTime();
        long fetchLatencyNanos = fetchEndTime - fetchStartTime;
        long fetchLatencyMillis = fetchLatencyNanos / 1_000_000;
        LOGGER.debug(
            "Connection ["
                + connectionUuid
                + "] Statement ["
                + statementId
                + "] Session ["
                + sessionInfo
                + "] Thrift getStatementResult fetch latency: "
                + fetchLatencyMillis
                + "ms");

        long getStatementResultEndTime = System.nanoTime();
        long getStatementResultLatencyNanos =
            getStatementResultEndTime - getStatementResultStartTime;
        long getStatementResultLatencyMillis = getStatementResultLatencyNanos / 1_000_000;
        LOGGER.debug(
            "Connection ["
                + connectionUuid
                + "] Statement ["
                + statementId
                + "] Session ["
                + sessionInfo
                + "] Thrift getStatementResult latency: "
                + getStatementResultLatencyMillis
                + "ms");

        return new DatabricksResultSet(
            new StatementStatus().setState(StatementState.SUCCEEDED),
            statementId,
            resultSet,
            StatementType.SQL,
            parentStatement,
            session);
      }
    } catch (TException e) {
      long getStatementResultEndTime = System.nanoTime();
      long getStatementResultLatencyNanos = getStatementResultEndTime - getStatementResultStartTime;
      long getStatementResultLatencyMillis = getStatementResultLatencyNanos / 1_000_000;
      LOGGER.debug(
          "Connection ["
              + connectionUuid
              + "] Statement ["
              + statementId
              + "] Session ["
              + sessionInfo
              + "] Thrift getStatementResult latency (with error): "
              + getStatementResultLatencyMillis
              + "ms");

      String errorMessage =
          String.format(
              "Error while receiving response from Thrift server. Request {%s}, Error {%s}",
              request.toString(), e.getMessage());
      LOGGER.error(e, errorMessage);
      throw new DatabricksHttpException(errorMessage, e, DatabricksDriverErrorCode.INVALID_STATE);
    }
    StatementStatus executionStatus = getStatementStatus(response);

    long getStatementResultEndTime = System.nanoTime();
    long getStatementResultLatencyNanos = getStatementResultEndTime - getStatementResultStartTime;
    long getStatementResultLatencyMillis = getStatementResultLatencyNanos / 1_000_000;
    LOGGER.debug(
        "Connection ["
            + connectionUuid
            + "] Statement ["
            + statementId
            + "] Session ["
            + sessionInfo
            + "] Thrift getStatementResult latency: "
            + getStatementResultLatencyMillis
            + "ms");

    return new DatabricksResultSet(
        executionStatus, statementId, resultSet, StatementType.SQL, parentStatement, session);
  }

  TCLIService.Client getThriftClient() {
    return thriftClient.get();
  }

  DatabricksConfig getDatabricksConfig() {
    return databricksConfig;
  }

  TFetchResultsResp getResultSetResp(
      TStatus responseStatus,
      TOperationHandle operationHandle,
      String context,
      int maxRowsPerBlock,
      boolean fetchMetadata)
      throws DatabricksHttpException {
    verifySuccessStatus(responseStatus, context);
    TFetchResultsReq request =
        new TFetchResultsReq()
            .setOperationHandle(operationHandle)
            .setFetchType((short) 0) // 0 represents Query output. 1 represents Log
            .setMaxRows(
                maxRowsPerBlock) // Max number of rows that should be returned in the rowset.
            .setMaxBytes(DEFAULT_BYTE_LIMIT);
    if (fetchMetadata
        && ProtocolFeatureUtil.supportsResultSetMetadataFromFetch(serverProtocolVersion)) {
      request.setIncludeResultSetMetadata(true); // fetch metadata if supported
    }
    TFetchResultsResp response;
    try {
      response = getThriftClient().FetchResults(request);
    } catch (TException e) {
      String errorMessage =
          String.format(
              "Error while fetching results from Thrift server. Request {%s}, Error {%s}",
              request.toString(), e.getMessage());
      LOGGER.error(e, errorMessage);
      throw new DatabricksHttpException(errorMessage, e, DatabricksDriverErrorCode.INVALID_STATE);
    }
    verifySuccessStatus(
        response.getStatus(),
        String.format(
            "Error while fetching results Request {%s}. TFetchResultsResp {%s}. ",
            request, response));
    return response;
  }

  private TFetchResultsResp listFunctions(TGetFunctionsReq request)
      throws TException, DatabricksSQLException {
    if (enableDirectResults) request.setGetDirectResults(DEFAULT_DIRECT_RESULTS);
    TGetFunctionsResp response = getThriftClient().GetFunctions(request);
    return fetchMetadataResults(response, response.toString());
  }

  private TFetchResultsResp listPrimaryKeys(TGetPrimaryKeysReq request)
      throws TException, DatabricksSQLException {
    if (enableDirectResults) request.setGetDirectResults(DEFAULT_DIRECT_RESULTS);
    TGetPrimaryKeysResp response = getThriftClient().GetPrimaryKeys(request);
    return fetchMetadataResults(response, response.toString());
  }

  private TFetchResultsResp listCrossReferences(TGetCrossReferenceReq request)
      throws TException, DatabricksSQLException {
    if (enableDirectResults) request.setGetDirectResults(DEFAULT_DIRECT_RESULTS);
    TGetCrossReferenceResp response = getThriftClient().GetCrossReference(request);
    return fetchMetadataResults(response, response.toString());
  }

  private TFetchResultsResp getTables(TGetTablesReq request)
      throws TException, DatabricksSQLException {
    if (enableDirectResults) request.setGetDirectResults(DEFAULT_DIRECT_RESULTS);
    TGetTablesResp response = getThriftClient().GetTables(request);
    return fetchMetadataResults(response, response.toString());
  }

  private TFetchResultsResp getTableTypes(TGetTableTypesReq request)
      throws TException, DatabricksSQLException {
    if (enableDirectResults) request.setGetDirectResults(DEFAULT_DIRECT_RESULTS);
    TGetTableTypesResp response = getThriftClient().GetTableTypes(request);
    return fetchMetadataResults(response, response.toString());
  }

  private TFetchResultsResp getCatalogs(TGetCatalogsReq request)
      throws TException, DatabricksSQLException {
    if (enableDirectResults) request.setGetDirectResults(DEFAULT_DIRECT_RESULTS);
    TGetCatalogsResp response = getThriftClient().GetCatalogs(request);
    return fetchMetadataResults(response, response.toString());
  }

  private TFetchResultsResp listSchemas(TGetSchemasReq request)
      throws TException, DatabricksSQLException {
    if (enableDirectResults) request.setGetDirectResults(DEFAULT_DIRECT_RESULTS);
    TGetSchemasResp response = getThriftClient().GetSchemas(request);
    return fetchMetadataResults(response, response.toString());
  }

  private TFetchResultsResp getTypeInfo(TGetTypeInfoReq request)
      throws TException, DatabricksSQLException {
    if (enableDirectResults) request.setGetDirectResults(DEFAULT_DIRECT_RESULTS);
    TGetTypeInfoResp response = getThriftClient().GetTypeInfo(request);
    return fetchMetadataResults(response, response.toString());
  }

  private TFetchResultsResp listColumns(TGetColumnsReq request)
      throws TException, DatabricksSQLException {
    if (enableDirectResults) request.setGetDirectResults(DEFAULT_DIRECT_RESULTS);
    TGetColumnsResp response = getThriftClient().GetColumns(request);
    return fetchMetadataResults(response, response.toString());
  }

  /**
   * Creates a new thrift client for the given endpoint URL and authentication headers.
   *
   * @param endPointUrl endpoint URL
   * @param databricksConfig SDK config object required for authentication headers
   */
  private TCLIService.Client createThriftClient(
      String endPointUrl,
      DatabricksConfig databricksConfig,
      IDatabricksConnectionContext connectionContext) {
    DatabricksHttpTTransport transport =
        new DatabricksHttpTTransport(
            DatabricksHttpClientFactory.getInstance().getClient(connectionContext),
            endPointUrl,
            databricksConfig,
            connectionContext);
    TBinaryProtocol protocol = new TBinaryProtocol(transport);

    return new TCLIService.Client(protocol);
  }

  /**
   * Fetches the metadata results from the given response object. If the response object contains a
   * directResults field, then the metadata results are fetched from the directResults field.
   * Otherwise, the metadata results are fetched by polling the operation status.
   *
   * @param response Thrift response object
   * @param contextDescription description of the context in which the response was received
   * @return metadata results {@link TFetchResultsResp}
   * @param <TResp> Thrift response type
   * @param <FResp> Thrift response field type
   * @throws TException if an error occurs while fetching the operation status during polling
   * @throws DatabricksSQLException if an error occurs while fetching the metadata results
   */
  private <TResp extends TBase<TResp, FResp>, FResp extends TFieldIdEnum>
      TFetchResultsResp fetchMetadataResults(TResp response, String contextDescription)
          throws TException, DatabricksSQLException {
    checkResponseForErrors(response);

    // Get the operation status from direct results if present
    TGetOperationStatusResp statusResp = null;
    FResp directResultsField = response.fieldForId(directResultsFieldId);
    if (response.isSet(directResultsField)) {
      TSparkDirectResults directResults =
          (TSparkDirectResults) response.getFieldValue(directResultsField);
      checkDirectResultsForErrorStatus(directResults, contextDescription);
      statusResp = directResults.getOperationStatus();
      checkOperationStatusForErrors(statusResp);
    }

    // Get the operation handle from the response
    FResp operationHandleField = response.fieldForId(operationHandleFieldId);
    TOperationHandle operationHandle =
        (TOperationHandle) response.getFieldValue(operationHandleField);

    // Polling until query operation state is finished
    TGetOperationStatusReq statusReq =
        new TGetOperationStatusReq()
            .setOperationHandle(operationHandle)
            .setGetProgressUpdate(false);
    while (shouldContinuePolling(statusResp)) {
      statusResp = getThriftClient().GetOperationStatus(statusReq);
      checkOperationStatusForErrors(statusResp);
    }

    if (hasResultDataInDirectResults(response)) {
      // The first response has result data
      // There is no polling in this case as status was already finished
      TSparkDirectResults directResults =
          (TSparkDirectResults) response.getFieldValue(directResultsField);
      return directResults.getResultSet();
    } else {
      // Fetch the result data after polling
      FResp statusField = response.fieldForId(statusFieldId);
      TStatus status = (TStatus) response.getFieldValue(statusField);
      return getResultSetResp(
          status, operationHandle, contextDescription, DEFAULT_ROW_LIMIT_PER_BLOCK, false);
    }
  }

  /**
   * Check the response for errors.
   *
   * @param response Thrift response object
   * @param <T> Thrift response type
   * @param <F> Thrift response field type
   * @throws DatabricksSQLException if the response contains an error status
   */
  private <T extends TBase<T, F>, F extends TFieldIdEnum> void checkResponseForErrors(
      TBase<T, F> response) throws DatabricksSQLException {
    F operationHandleField = response.fieldForId(operationHandleFieldId);
    F statusField = response.fieldForId(statusFieldId);
    TStatus status = (TStatus) response.getFieldValue(statusField);

    if (!response.isSet(operationHandleField) || isErrorStatusCode(status)) {
      // if the operationHandle has not been set, it is an error from the server.
      LOGGER.error("Error thrift response {}", response);
      throw new DatabricksSQLException(status.getErrorMessage(), status.getSqlState());
    }
  }

  private void checkOperationStatusForErrors(TGetOperationStatusResp statusResp)
      throws DatabricksSQLException {
    if (statusResp != null
        && statusResp.isSetOperationState()
        && isErrorOperationState(statusResp.getOperationState())) {
      String errorMsg =
          String.format("Operation failed with error: %s", statusResp.getErrorMessage());
      LOGGER.error(errorMsg);
      throw new DatabricksSQLException(errorMsg, statusResp.getSqlState());
    }
  }

  private boolean shouldContinuePolling(TGetOperationStatusResp statusResp) {
    return statusResp == null
        || !statusResp.isSetOperationState()
        || isPendingOperationState(statusResp.getOperationState());
  }

  private <T extends TBase<T, F>, F extends TFieldIdEnum> boolean hasResultDataInDirectResults(
      TBase<T, F> response) {
    F directResultsField = response.fieldForId(directResultsFieldId);
    if (!response.isSet(directResultsField)) {
      return false;
    }
    TSparkDirectResults directResults =
        (TSparkDirectResults) response.getFieldValue(directResultsField);
    return directResults.isSetResultSet() && directResults.isSetResultSetMetadata();
  }

  private boolean isErrorStatusCode(TStatus status) {
    if (status == null || !status.isSetStatusCode()) {
      LOGGER.error("Status code is not set, marking the response as failed");
      return true;
    }
    TStatusCode statusCode = status.getStatusCode();
    return statusCode == TStatusCode.ERROR_STATUS
        || statusCode == TStatusCode.INVALID_HANDLE_STATUS;
  }

  private boolean isErrorOperationState(TOperationState state) {
    return state == TOperationState.ERROR_STATE || state == TOperationState.CLOSED_STATE;
  }

  private boolean isPendingOperationState(TOperationState state) {
    return state == TOperationState.RUNNING_STATE || state == TOperationState.PENDING_STATE;
  }

  void setServerProtocolVersion(TProtocolVersion protocolVersion) {
    serverProtocolVersion = protocolVersion;
  }

  private TimeoutHandler getTimeoutHandler(TExecuteStatementResp response, int timeoutInSeconds) {
    final TOperationHandle operationHandle = response.getOperationHandle();

    return new TimeoutHandler(
        timeoutInSeconds,
        "Thrift Operation Handle: " + operationHandle.toString(),
        () -> {
          try {
            LOGGER.debug("Canceling operation due to timeout: {}", operationHandle);
            cancelOperation(new TCancelOperationReq().setOperationHandle(operationHandle));
          } catch (Exception e) {
            LOGGER.warn("Failed to cancel operation on timeout: {}", e.getMessage());
          }
        });
  }

  private TGetOperationStatusResp getOperationStatus(
      TGetOperationStatusReq statusReq, StatementId statementId) throws TException {
    long operationStatusStartTime = System.nanoTime();
    TGetOperationStatusResp operationStatus = getThriftClient().GetOperationStatus(statusReq);
    long operationStatusEndTime = System.nanoTime();
    long operationStatusLatencyMillis =
        (operationStatusEndTime - operationStatusStartTime) / 1_000_000;
    LOGGER.debug(
        "Statement [{}] Thrift operation status latency: {}ms",
        statementId,
        operationStatusLatencyMillis);
    TelemetryCollector.getInstance()
        .recordGetOperationStatus(statementId.toString(), operationStatusLatencyMillis);
    return operationStatus;
  }
}
