package com.databricks.jdbc.telemetry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.databricks.jdbc.api.internal.IDatabricksConnectionContext;
import com.databricks.jdbc.model.telemetry.StatementTelemetryDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TelemetryHelperTest {
  @Mock IDatabricksConnectionContext connectionContext;

  @Test
  void testErrorTelemetryLogDoesNotThrowError() {
    when(connectionContext.getConnectionUuid()).thenReturn("test-uuid");
    assertDoesNotThrow(
        () ->
            TelemetryHelper.exportFailureLog(
                connectionContext, "err", "msg", "test-statement-id", null));
  }

  @Test
  void testExportTelemetryLogDoesNotThrowError() {
    StatementTelemetryDetails details = new StatementTelemetryDetails("test-statement-id");
    try (MockedStatic<TelemetryHelper> mocked = mockStatic(TelemetryHelper.class)) {
      assertDoesNotThrow(() -> TelemetryHelper.exportTelemetryLog(details));
      ArgumentCaptor<StatementTelemetryDetails> captor =
          ArgumentCaptor.forClass(StatementTelemetryDetails.class);
      mocked.verify(() -> TelemetryHelper.exportTelemetryLog(captor.capture()));
      assertEquals("test-statement-id", captor.getValue().getStatementId());
    }
  }

  @Test
  void testGetDriverSystemConfigurationDoesNotThrowError() {
    assertDoesNotThrow(TelemetryHelper::getDriverSystemConfiguration);
  }
}
