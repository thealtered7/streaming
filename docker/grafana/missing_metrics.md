# Missing metrics for the CDC Grafana dashboard

This file lists dashboard panels requested for the CDC Pipeline dashboard that were **omitted** because current Prometheus series cannot support them. Application metrics were not changed as part of dashboard provisioning.

Service identity note: through the streaming Compose OTel collector scrape (`job=otel-collector`), Micrometer/OTLP `service.name` appears as the Prometheus label **`exported_job`** (not `service_name`). Spring apps also set **`application`**. Writer CPU/memory panels filter on `exported_job="open-tables-writer-bronze"` and `exported_job="open-tables-writer-silver"`.

---

## Streaming

| Desired panel | Why omitted / gap |
|---------------|-------------------|
| HTTP latency p50 / p75 / p90 | Spring exports `http_server_requests_milliseconds_bucket`, but live series only have `le="+Inf"`. Without finite buckets, `histogram_quantile` cannot compute percentiles. Observation timers (`geo_service_*_milliseconds_bucket`) have the same limitation. |
| HTTP by URI + status | **Included** via `http_server_requests_milliseconds_count{uri,status,method}`. App counters (`geo_service_*_requests_total`) are also charted; they label `status` but encode endpoint as the metric name, not `uri`. |

---

## datapipelines

| Desired panel | Why omitted / gap |
|---------------|-------------------|
| HTTP latency p50 / p75 / p90 | Same as Streaming: `http_server_requests_milliseconds_bucket` / `datapipelines_*_milliseconds_bucket` only expose `le="+Inf"` in Prometheus today. |
| HTTP by URI + status | **Included** via `http_server_requests_milliseconds_count`. App `datapipelines_*_requests_total` counters are also charted (operation + `status`). |

---

## pgoutput_to_json

| Desired panel | Why omitted / gap |
|---------------|-------------------|
| Records written to buffer by **extract_type** and **source table** | `pgoutput_to_json_cdc_event_written_to_buffer_write.records` only tags `outcome` on the counter. `table` is a span/timer attribute; `extract_type` is not on the write counter (only on `pgoutput_to_json_flush_notification` for some CDC publishes). |
| Buffer flushes | **Included** as `pgoutput_to_json_cdc_file_written_flush_records_total` by `outcome` (plus close records). May be empty until flush traffic is observed. Not broken down by extract_type / table for the same label reasons. |
| CPU / memory | **Included** filtered by `exported_job="pgoutput-to-json"`. Requires the process to export JVM binders with that resource `service.name`. |

---

## open_tables_writer

| Desired panel | Why omitted / gap |
|---------------|-------------------|
| Kafka raw-write messages by **extract_type** and **source table** | `table_writer_kafka_process_record.records` / `type2_dimension_kafka_process_record.records` only tag `outcome`. No `extract_type` metric label; `table` is on spans/timers, not the `.records` counter. |
| Bronze **rows** written | `iceberg_table_writer_write_to_table.records` counts per-file write attempts (`outcome`), not Iceberg row counts. |
| Silver **rows** written/merged | `type2_dimension_transformer_transform.records` counts per-transform attempts (`outcome`), not type1/type2 row counts. |
| Kafka process latency p50 / p75 / p90 by extract_type, source table, and write type (bronze / type1 / type2) | Process-record timers exist but lack those dimensions; histogram buckets are also only `le="+Inf"`. No label distinguishes bronze vs type1 vs type2 write type. |
| CPU / memory | **Included** for `exported_job="open-tables-writer-bronze"` and `exported_job="open-tables-writer-silver"`. Until daemons set those OTel `service.name` values, series will not match (today writer metrics appear under `exported_job="unknown_service"` and JVM/CPU binders are not present under that job). |

---

## Naming assumptions (verified against live Prometheus)

| Assumption | Observed |
|------------|----------|
| Counter suffix `_total` | Yes (e.g. `geo_service_scalar_list_requests_total`) |
| Timer unit | `_milliseconds_*` (not `_seconds_*`) via OTLP path |
| Spring HTTP metric | `http_server_requests_milliseconds_{count,sum,bucket}` with `uri`, `status`, `method`, `application` |
| Service identity label | `exported_job` (+ `application` for Spring) |
| Histogram usability for quantiles | Only `le="+Inf"` present → percentiles omitted |
