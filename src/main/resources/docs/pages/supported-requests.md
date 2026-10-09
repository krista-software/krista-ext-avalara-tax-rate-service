# Supported Requests

Avalara Tax Rate Service exposes **4 catalog requests** across **2 areas**. Each links to its
detailed page.

## Address
| Request | Type | Description |
|---|---|---|
| [Resolve Address](pages/ResolveAddress.md) | QUERY_SYSTEM | Validates/standardizes an address and returns its coordinates, resolution quality, and taxing authorities. |
| [List Jurisdictions By Address](pages/ListJurisdictionsByAddress.md) | QUERY_SYSTEM | Returns every taxing jurisdiction near an address, with authority type and tax/sales/use rates. |

## Setup
| Request | Type | Description |
|---|---|---|
| [Test Connection](pages/TestConnection.md) | QUERY_SYSTEM | Verifies the configured Account ID / License Key against AvaTax. |
| [Health Check](pages/HealthCheck.md) | QUERY_SYSTEM | Reports JVM health plus AvaTax reachability. |

All 4 requests are enabled as AI-agent (MCP) tools.
