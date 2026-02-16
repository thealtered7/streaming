#!/bin/bash
set -e

# Connect to the geo database and run the initialization SQL
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "geo" <<-EOSQL
    -- Set timezone to UTC for this session and as default for the database
    SET timezone = 'UTC';
    ALTER DATABASE geo SET timezone = 'UTC';
    
    -- Grant usage on the public schema
    GRANT USAGE ON SCHEMA public TO streaming_geo;
    GRANT USAGE ON SCHEMA public TO debezium;

    -- Create the geo_clients table matching the GeoClient entity
    CREATE TABLE IF NOT EXISTS geo_clients (
        id BIGSERIAL PRIMARY KEY,
        client_id UUID NOT NULL UNIQUE,
        created_at TIMESTAMPTZ NOT NULL,
        updated_at TIMESTAMPTZ NOT NULL
    );

    -- Create the scalars table matching the Scalar entity
    CREATE TABLE IF NOT EXISTS scalars (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        value DOUBLE PRECISION,
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    );

    -- Ensure defaults on existing scalars table (idempotent for re-runs)
    ALTER TABLE scalars ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
    ALTER TABLE scalars ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

    -- Full replication for CDC (required for UPDATE/DELETE to include full row)
    ALTER TABLE geo_clients REPLICA IDENTITY FULL;
    ALTER TABLE scalars REPLICA IDENTITY FULL;

    -- Publication for Debezium CDC
    DROP PUBLICATION IF EXISTS debezium_publication;
    CREATE PUBLICATION debezium_publication FOR TABLE geo_clients, scalars;

    -- Debezium needs SELECT for initial snapshot
    GRANT SELECT ON geo_clients TO debezium;
    GRANT SELECT ON scalars TO debezium;

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

    -- Grant privileges on the scalars table
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE scalars TO streaming_geo;

    -- Grant usage on the sequence for geo_clients
    GRANT USAGE, SELECT ON SEQUENCE geo_clients_id_seq TO streaming_geo;

    -- Grant usage on the sequence for scalars
    GRANT USAGE, SELECT ON SEQUENCE scalars_id_seq TO streaming_geo;

    -- Create indexes for better performance
    CREATE INDEX IF NOT EXISTS idx_geo_clients_client_id ON geo_clients(client_id);
    CREATE INDEX IF NOT EXISTS idx_geo_clients_created_at ON geo_clients(created_at);
    CREATE INDEX IF NOT EXISTS idx_scalars_name ON scalars(name);
    CREATE INDEX IF NOT EXISTS idx_scalars_created_at ON scalars(created_at);

    -- Trigger function to set updated_at on INSERT and UPDATE
    CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS \$\$
    BEGIN
        NEW.updated_at := CURRENT_TIMESTAMP;
        IF (TG_OP = 'INSERT' AND NEW.created_at IS NULL) THEN
            NEW.created_at := CURRENT_TIMESTAMP;
        END IF;
        RETURN NEW;
    END;
    \$\$ LANGUAGE plpgsql;

    -- Attach trigger to geo_clients
    DROP TRIGGER IF EXISTS trg_geo_clients_updated_at ON geo_clients;
    CREATE TRIGGER trg_geo_clients_updated_at
        BEFORE INSERT OR UPDATE ON geo_clients
        FOR EACH ROW EXECUTE FUNCTION set_updated_at();

    -- Attach trigger to scalars
    DROP TRIGGER IF EXISTS trg_scalars_updated_at ON scalars;
    CREATE TRIGGER trg_scalars_updated_at
        BEFORE INSERT OR UPDATE ON scalars
        FOR EACH ROW EXECUTE FUNCTION set_updated_at();
EOSQL

