# Authentication

Avalara Tax Rate Service authenticates with **HTTP Basic Authentication** — a static Account ID +
License Key pair issued by Avalara. There is no OAuth or interactive login flow.

## Obtaining credentials

1. Sign in to your Avalara **AvaTax Sandbox** account (a Sandbox account is separate from a
   Production AvaTax account — this extension is configured to call the Sandbox host only).
2. Under your account settings, locate your **Account ID** and generate/copy a **License Key**. The
   License Key is shown only once at creation time — store it securely.
3. Enter both values into the **Account ID** and **License Key** fields on the extension's Setup tab.

## Required scopes / permissions

| Scope / Permission | Why it's needed |
|---|---|
| Any AvaTax Sandbox user role with API access (e.g. AccountAdmin, AccountUser, CompanyUser) | Both requests (`Resolve Address`, `List Jurisdictions By Address`) call read-only AvaTax address/definition endpoints that only require a valid authenticated account — no elevated role is needed. |

## Verifying

Run **Test Connection** in the Setup area. It calls AvaTax's `GET /api/v2/utilities/ping` endpoint —
an endpoint that never itself errors — and checks the `authenticated` flag in the response. A
successful result confirms the Account ID and License Key are valid; a failure returns an actionable
message telling you to re-check those two fields.
