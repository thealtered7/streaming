.PHONY: help build docker-build docker-run docker-up docker-down clean test run geo-shell post-geoclient post-scalar put-scalar

# Default target
help:
	@echo "Available targets:"
	@echo "  build          - Build the project using Gradle"
	@echo "  test           - Run tests"
	@echo "  run            - Run the Spring Boot application"
	@echo "  docker-build   - Build the Docker image"
	@echo "  docker-run     - Run the Docker container"
	@echo "  docker-up      - Start services using docker-compose"
	@echo "  docker-down    - Stop services using docker-compose"
	@echo "  geo-shell      - Launch psql shell connected to geo database"
	@echo "  post-geoclient - POST a GeoClient to the /geo-clients endpoint"
	@echo "  post-scalar    - POST a Scalar to the /scalars endpoint"
	@echo "  put-scalar     - PUT to update a Scalar by ID"
	@echo "  clean          - Clean build artifacts"
	@echo "  clean-docker   - Remove Docker containers and images"

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

# Start services using docker-compose (in background)
docker-up:
	docker compose up --build -d

# Start services in detached mode
docker-up-d:
	docker compose up --build -d

# Stop services using docker-compose
docker-down:
	docker compose down

# Launch psql shell connected to geo database
# Note: Requires docker-compose services to be running (make docker-up-d)
geo-shell:
	docker run -it --rm \
		--network streaming_streaming \
		-e PGPASSWORD=postgres \
		postgres:16-alpine \
		psql -h postgres -U postgres -d geo

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

# Clean build artifacts
clean:
	./gradlew clean

# Clean Docker containers and images
clean-docker:
	docker compose down -v
	docker rmi streaming-app || true
	docker rmi streaming-streaming-app || true

# Full clean (build + docker)
clean-all: clean clean-docker

