# NEXT CHANGELOG

## [Unreleased]

### Added

### Updated

### Fixed

- Reclassify transient/mis-categorized server errors so callers can identify
  retryable failures: Unity Catalog unavailability (`UC_CLIENT_EXCEPTION` /
  `XXUCC`) and parquet read / connection-acquisition deadlines
  (`PARQUET_FAILED_READ_FOOTER`, `DEADLINE_EXCEEDED: acquiring connection`)
  are now reported with SQL state `08S01` (communication link failure).
  Server-side `ConcurrentModificationException` is now reported with SQL state
  `40001` (serialization failure) instead of the misleading `42000`.

---
*Note: When making changes, please add your change under the appropriate section
with a brief description.*
