# Overview

Avalara Tax Rate Service connects Krista to **Avalara AvaTax** via its REST v2 API. Use it to
validate/standardize a physical address and look up the taxing jurisdictions (authority and rates)
that apply to that address directly from Krista workflows and agents.

## Use cases

- **Resolve an address** — submit a street address and get back AvaTax's standardized version, its
  geocoordinates, a confidence rating for how precisely it was located, and the taxing authorities
  (e.g. the state) that govern it.
- **List jurisdictions for an address** — submit an address and get back every taxing jurisdiction
  AvaTax has on file for it, each with its tax rate, sales-tax rate, use-tax rate, and the type of
  taxing authority it represents.

## What you can do

The extension exposes 4 requests grouped by area (2 in **Address**, 2 in **Setup**) — see
[Supported Requests](pages/supported-requests.md) for the full list, or the sidebar for each
request's detailed page.

## Before you start

1. Obtain credentials — see [Authentication](pages/Authentication.md).
2. Configure the Setup tab — see [Configuration](pages/ExtensionConfiguration.md).
3. Run **Test Connection** (Setup area) to verify connectivity.

## Environment

This extension is configured to call the **AvaTax Sandbox** (`https://sandbox-rest.avatax.com`) —
not AvaTax Production. The Account ID and License Key entered on the Setup tab must be Sandbox
credentials.
