# Entities

Data structures returned by the requests. Request output tables link here instead of repeating the
full field list.

## Resolved Address
The result of resolving a physical address against Avalara's address-validation system: the
standardized address, its geocoordinates, a resolution-confidence rating, and the taxing authorities
that govern it. Returned by [Resolve Address](pages/ResolveAddress.md).

| Field | Type | Description |
|---|---|---|
| Line 1 | Text | The standardized first address line. Primary key for this entity. |
| City | Text | The standardized city. |
| Region | Text | The standardized state/province/region code. |
| Country | Text | The standardized two-character country code. |
| Postal Code | Text | The standardized postal/zip code. |
| Latitude | Number | Geocoded latitude. |
| Longitude | Number | Geocoded longitude. |
| Resolution Quality | Text | How precisely AvaTax located the address (e.g. `Intersection`, `ExactAddress`, `ZipCentroid`). |
| Tax Authorities | [ { AvalaraId: Text, JurisdictionName: Text, JurisdictionType: Text, SignatureCode: Text } ] | The taxing authorities (e.g. the state) that govern this address. |
| Messages | [ { Summary: Text, Details: Text, RefersTo: Text, Severity: Text, Source: Text } ] | Any validation messages AvaTax returned about this address (e.g. "not deliverable"). |

## Address Jurisdiction Match
One taxing-jurisdiction match for an address, for one effective-date range: the address AvaTax
matched against, and every taxing jurisdiction (authority type + rate/salesRate/useRate) that applies
to it. Returned by [List Jurisdictions By Address](pages/ListJurisdictionsByAddress.md).

| Field | Type | Description |
|---|---|---|
| Line 1 | Text | The address line AvaTax matched against. Primary key for this entity. |
| City | Text | The matched city. |
| Region | Text | The matched state/province/region code. |
| Postal Code | Text | The matched postal/zip code. |
| Country | Text | The matched two-character country code. |
| Description | Text | AvaTax's description of this jurisdiction match. |
| Boundary Level | Text | The geographic precision of the match (e.g. `Address`, `Zip5`, `Zip9`). |
| Is Default | Boolean | Whether this is AvaTax's default jurisdiction match for the address. |
| Effective Date | Date | Start of the date range this match applies for. |
| End Date | Date | End of the date range this match applies for. |
| Jurisdictions | [ { Code: Text, Name: Text, Type: Text, SignatureCode: Text, Region: Text, City: Text, County: Text, Country: Text, Rate: Number, SalesRate: Number, UseRate: Number, TaxAuthorityTypeId: Number, EffectiveDate: Date, EndDate: Date } ] | Every taxing jurisdiction (with its tax/sales/use rate and taxing authority type id) that applies to this address. |
