# Health Check

## Overview
Reports the extension's JVM health alongside a live AvaTax reachability check. Use this for periodic
monitoring of the extension's runtime, independent of any specific business request.

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
| Is Healthy | Boolean | `true` if the JVM is responsive and AvaTax accepted the configured credentials. |
| Heap Used MB | Number | Current JVM heap usage, in megabytes. |
| Details | Text | `OK`, a credential-rejection note, or the reachability failure reason. |

## Example
**Input**: _(none)_
**Output**: Is Healthy=`true`, Heap Used MB=`128.0`, Details=`OK`

## Error Handling
- If AvaTax is unreachable, `Is Healthy=false` and `Details` carries the exception message — this
  request never throws; it always returns a response so monitoring can poll it unattended.
