# NEXT CHANGELOG

## [Unreleased]

### Added

### Updated

### Fixed

- Reclassify transient/mis-categorized server errors so callers can identify
  retryable failures. The remap is applied at all Thrift error sites
  (`checkResponseForErrors`, `executeAsync`, `verifySuccessStatus`, and the
  polling status handler) so the same server failure surfaces with the same
  SQL state regardless of which response carries it.
  - Unity Catalog unavailability (`[UC_CLIENT_EXCEPTION]`, previously `XXUCC`)
    and parquet read / connection-acquisition deadlines
    (`[PARQUET_FAILED_READ_FOOTER]`, `DEADLINE_EXCEEDED: acquiring connection`)
    are now reported with SQL state `08S01` (communication link failure).
  - Server-side `java.util.ConcurrentModificationException` is now reported
    with SQL state `40001` (serialization failure) instead of the misleading
    `42000`. The remap only applies when the original SQL state is `42000` so
    unrelated `42xxx` states (e.g. `42501` insufficient privilege) are
    preserved.
  Notes for callers and operators:
  - Callers branching on the legacy `XXUCC`/`42000` states for these failures
    must update to `08S01`/`40001`. The driver logs the original→remapped
    state at `INFO` level for traceability.
  - The driver's failure telemetry uses `sqlState` as the error-name field,
    so dashboards/alerts keyed on `XXUCC` or `42000` for these specific
    failure modes will need to be updated to the new states.

---
*Note: When making changes, please add your change under the appropriate section
with a brief description.*
