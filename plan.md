# Avalara Tax Rate Service — Implementation Plan

## Summary
A Krista Global Catalog extension integrating Avalara's **AvaTax** REST API. It exposes two
read-only lookups: resolving a physical address (standardized address, geocoordinates, resolution
confidence, and the taxing authorities that govern it), and listing the full set of taxing
jurisdictions near an address, each with its tax rate, sales-tax rate, and use-tax rate. Target
environment: **AvaTax Sandbox** (`https://sandbox-rest.avatax.com`), per the user's explicit choice.

- **Extension name:** `avalara-tax-rate-service`
- **Title:** Avalara Tax Rate Service
- **Version:** 1.0.0
- **Package:** `app.krista.extensions.accounting.taxrateservice.avalarataxrateservice`
- **Ecosystem / Domain:** Accounting / Tax Rate Service

## Authentication
**Pattern A (two secured fields)** — HTTP Basic Authentication. The user's authentication answer
was "unsure," so the mechanism was determined from the API material: every request in the AvaTax
Postman collection declares `auth: { type: "basic" }`, a static, pre-issued credential pair
(Avalara **Account ID** as username, **License Key** as password) with no OAuth/login flow. The
Setup tab will have two secured text fields (Account ID, License Key) instead of a single token
field; no `RequestAuthenticator` or JAX-RS callback app is needed. `TEST_CONNECTION` /
`VALIDATE_ATTRIBUTES` will call a lightweight AvaTax endpoint (e.g. account ping) to confirm the
credentials work.

Base URL is **fixed** to `https://sandbox-rest.avatax.com` per the user's answer (not a
configurable Setup-tab field) — switching to production later requires a code change, not just a
config change.

## Area: Address

| Request | Inputs | Output | External endpoint | Reused / New |
|---|---|---|---|---|
| Resolve Address | line1, line2, line3, city, region, postalCode, country (all optional Text), textCase (optional pick-one: Upper/Lower/Mixed) | `Entity(ResolvedAddress)` | `POST /api/v2/addresses/resolve` | New |
| List Jurisdictions By Address | line1, line2, line3, city, region, postalCode, country (all optional Text), filter, orderBy (optional Text), top, skip (optional Number) | `[ Entity(AddressJurisdictionMatch) ]` | `GET /api/v2/definitions/jurisdictionsnearaddress` | New |

Both requests are read-only (`QUERY_SYSTEM`) — AvaTax also exposes a `GET` variant of
`addresses/resolve` with identical behavior to the `POST`; the `POST` form is used since it only
needs a single catalog request and handles the address payload as a JSON body. Both requests are
exposed as **MCP tools** (`tool = true`) — they're simple, safe lookups well suited to agent
invocation.

### Tests planned
- **Resolve Address:** full-address happy path (coordinates + taxAuthorities populated); AvaTax
  validation-message path (e.g. "undeliverable") surfaced without throwing; optional/blank fields
  omitted cleanly; AvaTax 400 (no usable address fields) mapped to a client-error response.
- **List Jurisdictions By Address:** happy path returning jurisdictions with `rate`/`salesRate`/
  `useRate`; `top`/`skip` paging and `@recordsetCount` surfaced as a count output; empty-result
  case; AvaTax error mapped to a client-error response.

## Entities

| Entity | Fields | Reused / New |
|---|---|---|
| **ResolvedAddress** | line1, city, region, country, postalCode (Text); latitude, longitude (Number); resolutionQuality (Text); taxAuthorities `[Entity(TaxAuthority)]`; messages `[Entity(AddressValidationMessage)]` | New |
| **TaxAuthority** | avalaraId, jurisdictionName, jurisdictionType, signatureCode (Text) | New |
| **AddressValidationMessage** | summary, details, refersTo, severity, source (Text) | New |
| **AddressJurisdictionMatch** | line1, city, region, postalCode, country, description, boundaryLevel (Text); isDefault (Boolean); effectiveDate, endDate (Date); jurisdictions `[Entity(TaxJurisdiction)]` | New |
| **TaxJurisdiction** | code, name, type, signatureCode, region, city, county, country (Text); rate, salesRate, useRate, taxAuthorityTypeId (Number); effectiveDate, endDate (Date) | New |

No raw `FreeForm`/`Map` passthrough — every AvaTax response object (including the nested
`taxAuthorities`, `messages`, and `jurisdictions` arrays) is modeled as its own typed entity.

## Capabilities
| Capability | Enabled? | Notes |
|---|---|---|
| Entity search | No | Neither object is a first-class searchable store in this scope — both are returned inline as part of a lookup response. |
| Webhooks / WAIT_FOR_EVENT | No | AvaTax address/jurisdiction lookups are synchronous; nothing to wait on. |
| MCP (`tool = true`) | **Yes** | Both requests are safe, read-only lookups — good agent-tool candidates. Requires `krista-apis` ≥ `1.0.125-sp1`. |
| Solutions tab | No | Not requested; only two requests in scope, no packaged solution content identified. |
| Binary files | No | No upload/download operations in scope. |

## Domain reuse
`domain-catalog.json` reported **`found: false`** for Accounting / Tax Rate Service — this is a
brand-new domain with no existing published entities or requests to check against. Nothing was
reused; all 2 requests and all 5 entities are newly built. Their names (`ResolvedAddress`,
`TaxAuthority`, `AddressValidationMessage`, `AddressJurisdictionMatch`, `TaxJurisdiction`) were
chosen to be vendor-agnostic so a future extension in this domain can reuse them rather than
inventing "Avalara"-prefixed equivalents.

## ⚠️ Placeholders needing replacement before deploy
- **`@Domain` id / `ecosystemId` / `ecosystemName`** for Accounting / Tax Rate Service — this
  ecosystem/domain pairing has never been published to the real global catalog
  (`domain-catalog.json` → `found: false`), so stable placeholder UUIDs will be minted to let the
  code compile. **These must be swapped for the real Krista-registered values before this
  extension can bind or deploy.**
- No other placeholders — base URL, auth mechanism, and both endpoints are fully determined from
  the API material and the user's answers.
