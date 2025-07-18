# NEXT CHANGELOG

## [Unreleased]

### Added
- Added DCO (Developer Certificate of Origin) check workflow for pull requests to ensure all commits are properly signed-off
- Added support for SSL client certificate authentication via parameter: SSLTrustStoreProvider

### Updated
- Column name support for JDBC ResultSet operations is now case-insensitive

### Fixed
- Fixed Bouncy Castle registration conflicts by using local provider instance instead of global security registration.
- Fixed Azure U2M authentication issue.
- Fixed unchecked exception thrown in delete session

---
*Note: When making changes, please add your change under the appropriate section with a brief description.* 
