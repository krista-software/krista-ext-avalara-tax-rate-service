# Release Notes

- **Developer Name** : Krista Extension Builder
- **Krista Service APIs Java** : 1.0.126
- **Global Catalog Version** : Not Available

## v1.0.0 (October 2026)

Initial release.

### New Features
* **Resolve Address** — validate/standardize a US or Canadian address and retrieve its coordinates, resolution quality, and taxing authorities.
* **List Jurisdictions By Address** — retrieve every taxing jurisdiction near an address, with each jurisdiction's tax rate, sales-tax rate, use-tax rate, and taxing authority type.
* **Test Connection** / **Health Check** — verify AvaTax connectivity and credentials from the Setup tab.

### Resolved Bugs
* Not Available

### Dependency Updates
* Not Available

### Known Issues
* Not Available

### Limitations and Caveats
* This build is configured to call the **AvaTax Sandbox** host only (`https://sandbox-rest.avatax.com`); it does not call AvaTax Production.
* The Accounting / Tax Rate Service domain is newly registered — no prior catalog entries existed to reuse.

<!-- Newest block on top; keep prior blocks untouched. This file is the @ChangeLog target. -->
