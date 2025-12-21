#!/bin/bash
set -e

# Connect to the geo database and run the initialization SQL
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "geo" <<-EOSQL
    -- Grant usage on the public schema
    GRANT USAGE ON SCHEMA public TO streaming_geo;

    -- Create the geo_clients table matching the GeoClient entity
    CREATE TABLE IF NOT EXISTS geo_clients (
        id BIGSERIAL PRIMARY KEY,
        client_id UUID NOT NULL UNIQUE,
        created_at TIMESTAMP NOT NULL,
        updated_at TIMESTAMP NOT NULL
    );

    -- Grant privileges on all existing tables in public schema
    GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO streaming_geo;

    -- Grant privileges on all existing sequences in public schema
    GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO streaming_geo;

    -- Set default privileges for future tables in public schema
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO streaming_geo;

    -- Set default privileges for future sequences in public schema
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO streaming_geo;

    -- Grant privileges on the geo_clients table
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE geo_clients TO streaming_geo;

    -- Grant usage on the sequence for geo_clients
    GRANT USAGE, SELECT ON SEQUENCE geo_clients_id_seq TO streaming_geo;

    -- Create indexes for better performance
    CREATE INDEX IF NOT EXISTS idx_geo_clients_client_id ON geo_clients(client_id);
    CREATE INDEX IF NOT EXISTS idx_geo_clients_created_at ON geo_clients(created_at);
EOSQL

