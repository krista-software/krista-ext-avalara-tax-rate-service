# Test Connection

## Overview
Validates connectivity and authentication against Avalara AvaTax by calling the vendor's own
connectivity-check endpoint. Use this after configuring the Setup tab, or whenever you suspect the
configured credentials have stopped working.

## Request Details
- **Area**: Setup
- **Type**: QUERY_SYSTEM
- **External API**: `GET /api/v2/utilities/ping`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
None.

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Success | Boolean | `true` if AvaTax accepted the configured credentials. |
| Message | Text | A human-readable confirmation (including the AvaTax server version) or an actionable failure reason. |

## Example
**Input**: _(none)_
**Output**: Success=`true`, Message=`Connected to AvaTax (server version 1.0.0.0).`

## Error Handling
- AvaTax's `ping` endpoint itself never returns an HTTP error — a bad Account ID / License Key comes
  back as HTTP 200 with `authenticated: false`, which this request surfaces as `Success=false` with an
  actionable message rather than a thrown error.
- A network-level failure (DNS, TLS, timeout) is surfaced as a SYSTEM-classified failure response.
