# Streaming

Spring Boot app with Kafka, Debezium CDC, and OpenTelemetry observability via docker-compose.

Start the stack:

```bash
make docker-up
```

## URLs

| URL | Purpose |
|-----|---------|
| http://localhost:3000 | Grafana (Explore → Tempo for traces, Prometheus for metrics) |
| http://localhost:9090 | Prometheus |
| http://localhost:3200 | Tempo API |
| http://localhost:8080 | Streaming app |
| http://localhost:8083 | Kafka Connect REST API |
| http://localhost:8081 | Schema Registry |
| http://localhost:9404/metrics | Kafka Connect / Debezium JMX metrics |
| http://localhost:4318 | OTLP HTTP (traces and metrics) |

Grafana login: `admin` / `admin`

## OpenTelemetry quick reference

All custom spans and request counters use the `geo_service` prefix.

### Controller spans and counters

| Endpoint | Span name | Counter name |
|----------|-----------|--------------|
| `GET /scalars` | `geo_service.scalar.list` | `geo_service.scalar.list.requests` |
| `GET /scalars/{id}` | `geo_service.scalar.get` | `geo_service.scalar.get.requests` |
| `POST /scalars` | `geo_service.scalar.create` | `geo_service.scalar.create.requests` |
| `PUT /scalars/{id}` | `geo_service.scalar.put` | `geo_service.scalar.put.requests` |
| `DELETE /scalars/{id}` | `geo_service.scalar.delete` | `geo_service.scalar.delete.requests` |
| `GET /health` | `geo_service.health` | `geo_service.health.requests` |
| `GET /geo-clients` | `geo_service.geo_client.list` | `geo_service.geo_client.list.requests` |
| `POST /geo-clients` | `geo_service.geo_client.create` | `geo_service.geo_client.create.requests` |
| `GET /geo-clients/{id}` | `geo_service.geo_client.get` | `geo_service.geo_client.get.requests` |
| `PUT /geo-clients/{id}` | `geo_service.geo_client.put` | `geo_service.geo_client.put.requests` |

Controller counters include label `status` (HTTP response code). Span tags include `http.status_code` and entity ids where applicable (`scalar.id`, `geo_client.id`).

### Service spans (no counters)

Service methods emit spans such as `geo_service.scalar_service.update_scalar` and `geo_service.geo_service.create_geo_client`.

### Traces (Grafana → Explore → Tempo)

| What to search | Example |
|----------------|---------|
| Controller span | `{ name = "geo_service.scalar.put" }` |
| Service span | `{ name = "geo_service.scalar_service.update_scalar" }` |
| Service | `{ resource.service.name = "streaming" }` |
| All custom spans | `{ name =~ "geo_service.*" }` |

Expected trace structure for `PUT /scalars/{id}`:

```
http put /scalars/{id}                      ← auto Spring/Micrometer span (root)
  └── geo_service.scalar.put                ← controller span
        └── geo_service.scalar_service.update_scalar   ← service span
              └── geo_service.scalar_service.get_scalar
```

### Metrics (Grafana → Explore → Prometheus)

| What to search | Example |
|----------------|---------|
| PUT counter (all statuses) | `geo_service_scalar_put_requests_total` |
| By status | `geo_service_scalar_put_requests_total{status="200"}` |
| All custom counters | `{__name__=~"geo_service_.*_requests_total"}` |

Micrometer names like `geo_service.scalar.put.requests` normalize to `geo_service_scalar_put_requests_total` in Prometheus (dots → underscores, `_total` suffix for counters). Series may also include `application="streaming"`.

### Code → Grafana lookup

| Code | Signal type | Grafana datasource | Search for |
|------|-------------|-------------------|------------|
| `geo_service.scalar.put` | Span | Tempo | `geo_service.scalar.put` |
| `geo_service.scalar_service.update_scalar` | Span | Tempo | `geo_service.scalar_service.update_scalar` |
| `geo_service.scalar.put.requests` | Counter | Prometheus | `geo_service_scalar_put_requests_total` |
