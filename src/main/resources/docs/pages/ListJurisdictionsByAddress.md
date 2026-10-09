# List Jurisdictions By Address

## Overview
Returns every Avalara-supported taxing jurisdiction that applies near an address, including each
jurisdiction's taxing authority type, tax rate, sales-tax rate, and use-tax rate. Use this when you
need the actual tax rates for an address, not just its standardized form.

## Request Details
- **Area**: Address
- **Type**: QUERY_SYSTEM
- **External API**: `GET /api/v2/definitions/jurisdictionsnearaddress`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Line 1 | Text | No | The first address line. |
| Line 2 | Text | No | The second address line. |
| Line 3 | Text | No | The third address line. |
| City | Text | No | The city portion of the address. |
| Region | Text | No | The state/province/region code. |
| Postal Code | Text | No | The postal/zip code. |
| Country | Text | No | The two-character ISO-3166 country code. |
| Filter | Text | No | An AvaTax `$filter` expression to narrow results. |
| Top | Number | No | Maximum number of records to return (pagination). |
| Skip | Number | No | Number of records to skip (pagination). |
| Order By | Text | No | A comma-separated sort expression, e.g. `id ASC`. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Address Jurisdictions | [ Entity(Address Jurisdiction Match) ] | One entry per effective-date range AvaTax matched for the address, each carrying the applicable jurisdictions. See [Entities](pages/entities.md#address-jurisdiction-match). |
| Count | Number | Number of Address Jurisdictions entries returned in this page. |

## Example
**Input**: Line 1=`5500 Irvine Center Dr`, City=`Irvine`, Region=`CA`, Postal Code=`92618`, Country=`US`
**Output**: Address Jurisdictions=`[{Line1: "5500 Irvine Center Dr", Jurisdictions: [{Code: "075", Name: "LAWRENCE", Type: "Country", Rate: 0.0825, SalesRate: 0.0725, UseRate: 0.0725, ...}]}]`, Count=`1`

## Error Handling
- **400 Bad Request** — the address could not be parsed/located, or an invalid `$filter`/`$orderBy`
  expression was supplied.
- **401/403** — the configured Account ID / License Key were not accepted; check the Setup tab.
- An address AvaTax has no jurisdiction data for returns an empty `Address Jurisdictions` list with
  `Count=0`, not an error.
