# Configuration

The extension is configured through the **Setup** tab in Krista. Configuration is stored per-invoker
— each invoker keeps its own independent connection and settings.

## Setup tab fields

| Field | Required | Default | Description |
|-------|----------|---------|-------------|
| Account ID | Yes | -- | Your Avalara AvaTax Sandbox Account ID. Sent as the username of an HTTP Basic Authentication header on every request. Secured (masked) on save. |
| License Key | Yes | -- | Your Avalara AvaTax Sandbox License Key. Sent as the password of an HTTP Basic Authentication header on every request. Secured (masked) on save. |

<!-- Pulled verbatim from the @Field.Text annotations on AvalaraTaxRateServiceExtension. -->

## Required vs optional

The extension validates these fields on save:

1. **Account ID** — must be present and non-empty.
2. **License Key** — must be present and non-empty.

## Notes

- The target host is **fixed to the AvaTax Sandbox** (`https://sandbox-rest.avatax.com`) for this
  deployment. It is not a Setup-tab field — switching to AvaTax Production requires a new build with
  a different base URL, not a configuration change.
- Credentials are validated with a live call (AvaTax's `ping` utility) whenever you click
  **Test Connection** — see [Authentication](pages/Authentication.md).
