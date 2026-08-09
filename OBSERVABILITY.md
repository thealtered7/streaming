# Observability

Micrometer Observation bridged to OpenTelemetry (OTLP traces and metrics). Naming helpers live in `Observability.java`. See also the README “OpenTelemetry quick reference” section.

| | |
|--|--|
| Prefix | `geo_service` |
| Spring / OTel service name | `streaming` |
| Span name | `geo_service.{operation}` |
| HTTP counter | `geo_service.{operation}.requests` (tag `status`) |
| Common registry tag | `application=streaming` |
| Export | OTLP traces + metrics (15s step); Prometheus via OTel collector |

HTTP observations set `http.status_code` on the span and increment the counter with the same value as tag `status` (`200` default; `ResponseStatusException` status; other runtime exceptions → `500`). Service observations are spans only (no counters, no attributes).

Spring Boot Actuator / Micrometer also export JVM and HTTP server meters; those are not listed here. `DefaultMeterObservationHandler` may record timers named like each observation.

---

## Spans

### HTTP / controller spans

| Span name | Represents | Attributes |
|-----------|------------|------------|
| `geo_service.scalar.list` | `GET /scalars` | `scalar.offset`, `scalar.count`, `http.status_code` |
| `geo_service.scalar.get` | `GET /scalars/{id}` | `scalar.id`, `http.status_code` |
| `geo_service.scalar.create` | `POST /scalars` | `http.status_code` |
| `geo_service.scalar.put` | `PUT /scalars/{id}` | `scalar.id`, `http.status_code` |
| `geo_service.scalar.delete` | `DELETE /scalars/{id}` | `scalar.id`, `http.status_code` |
| `geo_service.wide.list` | `GET /wide` | `wide.offset`, `wide.count`, `http.status_code` |
| `geo_service.wide.get` | `GET /wide/{id}` | `wide.id`, `http.status_code` |
| `geo_service.wide.create` | `POST /wide` | `http.status_code` |
| `geo_service.wide.put` | `PUT /wide/{id}` | `wide.id`, `http.status_code` |
| `geo_service.wide.delete` | `DELETE /wide/{id}` | `wide.id`, `http.status_code` |
| `geo_service.health` | `GET /health` | `http.status_code` |
| `geo_service.geo_client.list` | `GET /geo-clients` | `http.status_code` |
| `geo_service.geo_client.create` | `POST /geo-clients` | `http.status_code` |
| `geo_service.geo_client.get` | `GET /geo-clients/{id}` | `geo_client.id`, `http.status_code` |
| `geo_service.geo_client.put` | `PUT /geo-clients/{id}` | `geo_client.id`, `http.status_code` |

### Scalar service spans (no counters)

| Span name | Represents |
|-----------|------------|
| `geo_service.scalar_service.create_scalar` | Persist new scalar |
| `geo_service.scalar_service.get_scalar` | Find scalar by id |
| `geo_service.scalar_service.update_scalar` | Save updated scalar |
| `geo_service.scalar_service.delete_scalar` | Delete by id |
| `geo_service.scalar_service.get_all_scalars` | Paginated list |
| `geo_service.scalar_service.get_scalar_by_name` | Find by name |
| `geo_service.scalar_service.exists_scalar_by_name` | Exists-by-name check |
| `geo_service.scalar_service.count_scalars` | Count rows |
| `geo_service.scalar_service.exists_scalar_by_id` | Exists-by-id check |

### Wide service spans (no counters)

| Span name | Represents |
|-----------|------------|
| `geo_service.wide_service.create_wide` | Persist new wide row |
| `geo_service.wide_service.get_wide` | Find wide by id |
| `geo_service.wide_service.update_wide` | Save updated wide row |
| `geo_service.wide_service.delete_wide` | Delete by id |
| `geo_service.wide_service.get_all_wides` | Paginated list |
| `geo_service.wide_service.count_wides` | Count rows |
| `geo_service.wide_service.exists_wide_by_id` | Exists-by-id check |

### Geo service spans (no counters)

Operation strings already include `geo_service.`, so the helper double-prefixes:

| Span name | Represents |
|-----------|------------|
| `geo_service.geo_service.create_geo_client` | Save new geo client |
| `geo_service.geo_service.get_geo_client` | Find by id |
| `geo_service.geo_service.update_geo_client` | Save update |
| `geo_service.geo_service.delete_geo_client` | Delete by id |
| `geo_service.geo_service.get_all_geo_clients` | List all |
| `geo_service.geo_service.get_geo_client_by_guid` | Find by GUID |
| `geo_service.geo_service.exists_geo_client_by_guid` | Exists-by-GUID |
| `geo_service.geo_service.exists_geo_client_by_id` | Exists-by-id |
| `geo_service.geo_service.count_geo_clients` | Count clients |

---

## Metrics (application counters)

| Metric name | Type | Measures | Labels |
|-------------|------|----------|--------|
| `geo_service.scalar.list.requests` | Counter | `GET /scalars` | `status`; `application=streaming` |
| `geo_service.scalar.get.requests` | Counter | `GET /scalars/{id}` | `status` |
| `geo_service.scalar.create.requests` | Counter | `POST /scalars` | `status` |
| `geo_service.scalar.put.requests` | Counter | `PUT /scalars/{id}` | `status` |
| `geo_service.scalar.delete.requests` | Counter | `DELETE /scalars/{id}` | `status` |
| `geo_service.wide.list.requests` | Counter | `GET /wide` | `status` |
| `geo_service.wide.get.requests` | Counter | `GET /wide/{id}` | `status` |
| `geo_service.wide.create.requests` | Counter | `POST /wide` | `status` |
| `geo_service.wide.put.requests` | Counter | `PUT /wide/{id}` | `status` |
| `geo_service.wide.delete.requests` | Counter | `DELETE /wide/{id}` | `status` |
| `geo_service.health.requests` | Counter | `GET /health` | `status` |
| `geo_service.geo_client.list.requests` | Counter | `GET /geo-clients` | `status` |
| `geo_service.geo_client.create.requests` | Counter | `POST /geo-clients` | `status` |
| `geo_service.geo_client.get.requests` | Counter | `GET /geo-clients/{id}` | `status` |
| `geo_service.geo_client.put.requests` | Counter | `PUT /geo-clients/{id}` | `status` |

In Prometheus, dots become underscores and counters typically get a `_total` suffix (e.g. `geo_service_scalar_list_requests_total`).

---

## Kafka Connect / Debezium (infra scrape)

Not application Observation code. The Compose stack scrapes Connect JMX via the agent on port `9404`, with rename rules in `docker/kafka-connect/jmx-config.yml`. Metric **families** (suffix is the JMX attribute name at scrape time):

| Family pattern | Labels |
|----------------|--------|
| `kafka_connect_worker_*` | — |
| `kafka_connect_connector_*` | `connector` |
| `kafka_connect_task_*` | `connector`, `task` |
| `kafka_connect_task_error_*` | `connector`, `task` |
| `debezium_postgres_*` | `context`, `server` |
| `jvm_memory_heap_*` | — |
| `jvm_gc_*` | `gc` |
