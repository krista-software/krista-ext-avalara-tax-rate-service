# Resolve Address

## Overview
Resolves/validates a US or Canadian address against Avalara's address-validation system, returning
the standardized address, its geocoordinates, a resolution-confidence rating, and the taxing
authorities that govern it. Use this when you need to confirm an address is deliverable/well-formed
and find out which jurisdiction (state, etc.) it belongs to before computing tax.

## Request Details
- **Area**: Address
- **Type**: QUERY_SYSTEM
- **External API**: `POST /api/v2/addresses/resolve`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Line 1 | Text | No | The first address line (street number and name). |
| Line 2 | Text | No | The second address line. |
| Line 3 | Text | No | The third address line. |
| City | Text | No | The city portion of the address. |
| Region | Text | No | The state/province/region code. |
| Postal Code | Text | No | The postal/zip code. |
| Country | Text | No | The two-character ISO-3166 country code. |
| Text Case | Pick One (Upper / Lower / Mixed) | No | Casing to apply to the resolved address text. |

At least one of Postal Code, (Line 1 + City + Region), or (Line 1 + Postal Code) must be supplied —
AvaTax returns a validation error otherwise (see Error Handling below).

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Resolved Address | Entity(Resolved Address) | The standardized address, coordinates, resolution quality, tax authorities, and any validation messages. See [Entities](pages/entities.md#resolved-address). |

## Example
**Input**: Line 1=`2000 Main Street`, City=`Irvine`, Region=`CA`, Country=`US`, Postal Code=`92614`
**Output**: Resolved Address with Line 1=`2000 Main Street`, Resolution Quality=`Intersection`,
Tax Authorities=`[{AvalaraId: AGAM, JurisdictionName: CALIFORNIA, JurisdictionType: State, SignatureCode: AGAM}]`

## Error Handling
- **400 Bad Request** — none of the usable address-field combinations were supplied; add at least a
  Postal Code or a Line 1 + City + Region.
- **401/403** — the configured Account ID / License Key were not accepted; check the Setup tab.
- AvaTax can return HTTP 200 with a populated `Messages` list (e.g. "address is not deliverable")
  rather than an error — always check the `Messages` output field, not just the HTTP status.
