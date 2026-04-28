# NEXT CHANGELOG

## [Unreleased]

### Added

### Updated

### Fixed
- Fixed `?` characters inside SQL comments, string literals, and quoted identifiers being incorrectly counted as parameter placeholders when `supportManyParameters=1`. `SQLInterpolator` now uses `SqlCommentParser` to locate only real placeholders. Fixes #1331.

---
*Note: When making changes, please add your change under the appropriate section
with a brief description.*
