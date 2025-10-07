# Changelog

All notable changes to the Ordergroove App will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-10-07

### Added
- Initial release of Ordergroove App for Gladly Sidekick
- **Actions**: Core subscription management functionality
  - `getSubscriptionsByCustomerId` - Retrieve all subscriptions for a customer
  - `getSubscriptionById` - Retrieve single subscription by ID
  - `getCancellationReasons` - Get available cancellation reasons (standard, comprehensive, merchant-specific)
  - `getOrderItems` - Retrieve all items for a specific order
  - `getShippingAddress` - Get shipping address details by address ID
  - `cancelSubscription` - Cancel a subscription with reason tracking
  - `reactivateSubscription` - Reactivate canceled subscriptions with new frequency settings
  - `skipSubscription` - Skip next scheduled order while keeping subscriptions active

- **Data Synchronization**: Comprehensive data ingestion capabilities
  - Customer data sync (`ordergroove_customer`) with profile information
  - Subscription data sync (`ordergroove_subscription`) with frequency and pricing details
  - Order data sync (`ordergroove_order`) with transaction history
  - Item data sync (`ordergroove_item`) with product and pricing information
  - Product data sync (`ordergroove_product`) with catalog and subscription eligibility
  - Incremental sync support with parent-child relationship mapping
  - External ID mapping for efficient data correlation

- **Authentication**: API key-based authentication via headers
  - Configurable `ORDERGROOVE_API_KEY` custom attribute support
  - Secure header-based authentication for all API calls

- **Testing Infrastructure**: Comprehensive test coverage
  - Unit tests for all actions with multiple scenarios
  - Integration test support for live API validation
  - Test data covering edge cases, error conditions, and successful operations
  - Mock data and expected transformation validation

### Features
- Support for standard, comprehensive, and merchant-specific cancellation reasons
- Detailed subscription frequency management (days, weeks, months, years)
- Order skipping functionality for delivery timing adjustments
- Complete shipping address and customer contact information retrieval
- Subscription reactivation with customizable start dates and frequencies
- Comprehensive order item details including pricing and product associations

### API Integration
- Full integration with Ordergroove REST API
- Support for GET operations (data retrieval) and PATCH operations (subscription management)
- Error handling for common scenarios (404 Not Found, 400 Bad Request, 403 Forbidden)
- Response transformation for optimal Gladly Sidekick integration

### Documentation
- Complete README with action descriptions and API implementations
- GraphQL schema definitions for all data types and operations
- Test case documentation with expected inputs and outputs
- Configuration and setup instructions
- Troubleshooting guide for common issues