.PHONY: help build docker-build docker-run docker-up docker-down clean test run geo-shell

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

