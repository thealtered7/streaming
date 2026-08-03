.PHONY: help build docker-build docker-run docker-up docker-down docker-stop clean test run geo-shell extract-pipeline extract-fart post-geoclient post-scalar put-scalar register-debezium scalars-to-stdout cdc-file-write-to-stdout kafka-topics debezium-status debezium-logs service-client

# Default target
help:
	@echo "Available targets:"
	@echo "  build          - Build the project using Gradle"
	@echo "  test           - Run tests"
	@echo "  run            - Run the Spring Boot application"
	@echo "  docker-build   - Build the Docker image"
	@echo "  docker-run     - Run the Docker container"
	@echo "  docker-stop-streaming-webapp    - Stop the streaming-webapp container"
	@echo "  docker-up      - Start services using docker-compose"
	@echo "  docker-down    - Stop services using docker-compose"
	@echo "  geo-shell      - Launch psql shell connected to geo database"
	@echo "  extract-pipeline - Extract WAL changes via pg_recvlogical to data/extract_pipeline/"
	@echo "  extract-fart       - Extract WAL via pg_recvlogical from fart slot (pglogical) to data/extract_pipeline/"
	@echo "  post-geoclient - POST a GeoClient to the /geo-clients endpoint"
	@echo "  post-scalar    - POST a Scalar to the /scalars endpoint"
	@echo "  put-scalar       - PUT to update a Scalar by ID"
	@echo "  register-debezium  - Register Debezium PostgreSQL connector (requires docker-up)"
	@echo "  debezium-status   - Show Debezium connector status (debug)"
	@echo "  debezium-logs     - Show Kafka Connect logs (debug)"
	@echo "  scalars-to-stdout  - Print scalar CDC topic contents from Kafka (requires docker-up)"
	@echo "  cdc-file-write-to-stdout - Print cdc-file-write topic (JSON Schema) from Kafka to stdout (requires docker-up)"
	@echo "  kafka-topics     - List Kafka topics (requires docker-up)"
	@echo "  clean          - Clean build artifacts"
	@echo "  clean-docker   - Remove Docker containers and images"
	@echo "  put-lots-of-scalars - Put 1000 scalars to the /scalars endpoint"
	@echo "  service-client   - Run the Java service-client CLI (e.g. make service-client ARGS='generate-scalars --scalar-count=100')"

# Build the project
build:
	./gradlew build

# Run tests
test:
	./gradlew test

# Run the application
run:
	./gradlew bootRun

# Build Docker image using Gradle
docker-build:
	./gradlew dockerBuild

# Run Docker container using Gradle
docker-run:
	./gradlew dockerRun

# Stop the streaming-webapp container
docker-stop-streaming-webapp:
	docker stop streaming-webapp 2>/dev/null || true

# Start services using docker-compose (in background)
docker-up:
	docker compose up --build -d

# Start services in detached mode
docker-up-d:
	docker compose up --build -d

# Stop services using docker-compose
docker-down:
	docker compose down

# List Kafka topics (useful to verify geo.public.scalars exists)
kafka-topics:
	docker compose exec kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# Consume and print scalar CDC topic (geo.public.scalars) to stdout
# Note: Requires docker-up, register-debezium, and data in scalars table.
# Run 'make post-scalar' in another terminal to insert data, or use the app.
# Uses docker exec if schema-registry is running; otherwise runs a one-off container
scalars-to-stdout:
	@if docker exec streaming-schema-registry echo >/dev/null 2>&1; then \
		docker exec -it streaming-schema-registry \
			kafka-avro-console-consumer \
			--bootstrap-server kafka:9092 \
			--topic geo.public.scalars \
			--from-beginning \
			--property schema.registry.url=http://localhost:8081; \
	else \
		docker run -it --rm --network host \
			confluentinc/cp-schema-registry:7.5.0 \
			kafka-avro-console-consumer \
			--bootstrap-server localhost:9092 \
			--topic geo.public.scalars \
			--from-beginning \
			--property schema.registry.url=http://localhost:8081; \
	fi

# Consume and print cdc-file-write topic (JSON Schema via Schema Registry) to stdout
# Note: Requires docker-up and messages on the cdc-file-write topic
cdc-file-write-to-stdout:
	@if docker exec streaming-schema-registry echo >/dev/null 2>&1; then \
		docker exec -it streaming-schema-registry \
			kafka-json-schema-console-consumer \
			--bootstrap-server kafka:9092 \
			--topic cdc-file-write \
			--from-beginning \
			--property schema.registry.url=http://localhost:8081; \
	else \
		docker run -it --rm --network host \
			confluentinc/cp-schema-registry:7.5.0 \
			kafka-json-schema-console-consumer \
			--bootstrap-server localhost:9092 \
			--topic cdc-file-write \
			--from-beginning \
			--property schema.registry.url=http://localhost:8081; \
	fi

# Show Debezium connector status (useful for debugging)
debezium-status:
	@echo "=== Connectors ==="
	@curl -s http://localhost:8083/connectors
	@echo ""
	@echo ""
	@echo "=== Connector status ==="
	@curl -s http://localhost:8083/connectors/streaming-postgres-connector/status
	@echo ""
	@echo ""
	@echo "=== Connector config ==="
	@curl -s http://localhost:8083/connectors/streaming-postgres-connector
	@echo ""

# Show Kafka Connect logs
debezium-logs:
	docker compose logs kafka-connect --tail=100

# Register Debezium PostgreSQL connector with Kafka Connect
# Note: Run after docker-up; may need to wait ~30s for Kafka Connect to be ready
register-debezium:
	@echo "Registering Debezium PostgreSQL connector..."
	@echo "Response:"
	@curl -s -w "\nHTTP %{http_code}\n" -X POST -H "Content-Type: application/json" \
		--data @docker/kafka-connect/postgres-connector.json \
		http://localhost:8083/connectors

# Launch psql shell connected to geo database
# Note: Requires docker-compose services to be running (make docker-up-d)
geo-shell:
	docker run -it --rm \
		--network streaming_streaming \
		-e PGPASSWORD=postgres \
		postgres:16-alpine \
		psql -h postgres -U postgres -d geo


# Extract WAL changes from extract_slot up to current LSN via pg_recvlogical
# Note: Requires docker-compose services to be running (make docker-up)
extract-pipeline:
	@mkdir -p data/extract_pipeline
	@END_LSN=$$(docker run --rm \
		--network streaming_streaming \
		-e PGPASSWORD=postgres \
		postgres:16-alpine \
		psql -h postgres -U postgres -d geo -tAc "SELECT pg_current_wal_lsn();"); \
	OUTFILE=extract-$$(date +%Y%m%dT%H%M%S).json; \
	echo "Extracting WAL changes up to $$END_LSN..."; \
	docker run --rm \
		--network streaming_streaming \
		-v "$$(pwd)/data/extract_pipeline:/data/extract_pipeline" \
		-e PGPASSWORD=extract_pipeline_user \
		postgres:16-alpine \
		pg_recvlogical \
			-h postgres \
			-d geo \
			-U extract_pipeline_user \
			--slot extract_slot \
			--start \
			--no-loop \
			--endpos="$$END_LSN" \
			-o proto_version=1 \
			-o publication_names=extract_publication \
			-f /data/extract_pipeline/$$OUTFILE && \
	echo "Done. Output: data/extract_pipeline/$$OUTFILE"

# Extract WAL changes from fart slot (pglogical) up to current LSN via pg_recvlogical
# Note: Requires docker-compose services to be running; slot fart must exist with pglogical plugin
extract-fart:
	@mkdir -p data/extract_pipeline
	@END_LSN=$$(docker run --rm \
		--network streaming_streaming \
		-e PGPASSWORD=postgres \
		postgres:16-alpine \
		psql -h postgres -U postgres -d geo -tAc "SELECT pg_current_wal_lsn();"); \
	OUTFILE=fart-$$(date +%Y%m%dT%H%M%S).json; \
	echo "Extracting WAL changes from fart up to $$END_LSN..."; \
	docker run --rm \
		--network streaming_streaming \
		-v "$$(pwd)/data/extract_pipeline:/data/extract_pipeline" \
		-e PGPASSWORD=extract_pipeline_user \
		postgres:16-alpine \
		pg_recvlogical \
			-h postgres \
			-d geo \
			-U extract_pipeline_user \
			--slot fart \
			--start \
			--no-loop \
			--endpos="$$END_LSN" \
			-o min_proto_version=1 \
			-o max_proto_version=1 \
			-o startup_params_format=1 \
			-o proto_format=json \
			-f /data/extract_pipeline/$$OUTFILE && \
	echo "Done. Output: data/extract_pipeline/$$OUTFILE"

# POST a GeoClient to the /geo-clients endpoint
# Usage: make post-geoclient [GUID=550e8400-e29b-41d4-a716-446655440000]
# Note: Requires streaming-app container to be running and accessible on localhost:8080
post-geoclient:
	@GUID=$${GUID:-$$(uuidgen 2>/dev/null || python3 -c "import uuid; print(uuid.uuid4())")}; \
	curl -X POST http://localhost:8080/geo-clients \
		-H "Content-Type: application/json" \
		-d "{\"guid\": \"$$GUID\"}" \
		-w "\n" \
		-s

# POST a Scalar to the /scalars endpoint
# Usage: make post-scalar [NAME=my-scalar] [VALUE=42.5]
# Note: Requires application to be running on localhost:8080
post-scalar:
	@NAME=$${NAME:-my-scalar}; \
	VALUE=$${VALUE:-}; \
	BODY=$$(if [ -n "$$VALUE" ]; then echo "{\"name\": \"$$NAME\", \"value\": $$VALUE}"; else echo "{\"name\": \"$$NAME\"}"; fi); \
	curl -X POST http://localhost:8080/scalars \
		-H "Content-Type: application/json" \
		-d "$$BODY" \
		-w "\n" \
		-s

# PUT to update a Scalar by ID
# Usage: make put-scalar ID=1 VALUE=42.5 [NAME=existing-name]
# Note: NAME is required in the request body; use the existing scalar's name to avoid overwriting it
# Requires application to be running on localhost:8080
put-scalar:
	@if [ -z "$$ID" ]; then echo "Error: ID is required. Usage: make put-scalar ID=1 VALUE=42.5 [NAME=my-scalar]"; exit 1; fi; \
	if [ -z "$$VALUE" ]; then echo "Error: VALUE is required. Usage: make put-scalar ID=$$ID VALUE=42.5 [NAME=my-scalar]"; exit 1; fi; \
	NAME=$${NAME:-scalar}; \
	curl -X PUT http://localhost:8080/scalars/$$ID \
		-H "Content-Type: application/json" \
		-d "{\"name\": \"$$NAME\", \"value\": $$VALUE}" \
		-w "\n" \
		-s

# Run the service-client CLI
# Usage: make service-client ARGS='generate-scalars --scalar-count=100'
service-client:
	@./bin/service-client $(ARGS)

generate-scalars:
	./bin/service-client generate-scalars --scalar-count=1000000

mutate-scalars:
	./bin/service-client mutate-scalars --scalar-count=10000

# Clean build artifacts
clean:
	./gradlew clean

# Clean Docker containers and images
clean-docker:
	docker compose down -v
	docker rmi streaming-app || true
	docker rmi streaming-streaming-app || true
	docker rmi streaming-kafka-connect:latest || true

# Full clean (build + docker)
clean-all: clean clean-docker


