#!/bin/bash
set -e

# Extract pipeline: native publication extract_publication + pgoutput slot extract_slot
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "geo" <<-EOSQL
    DROP PUBLICATION IF EXISTS extract_publication;
    CREATE PUBLICATION extract_publication FOR TABLES IN SCHEMA public;

    -- Table SELECT + REPLICATION on extract_pipeline_user (via wal_reader_role) is sufficient for pgoutput.

    DO \$\$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_replication_slots WHERE slot_name = 'extract_slot'
        ) THEN
            PERFORM pg_create_logical_replication_slot('extract_slot', 'pgoutput');
        END IF;
    END;
    \$\$;
EOSQL
