-- Create the geo database
CREATE DATABASE geo;

-- Create the streaming_geo user
CREATE USER streaming_geo WITH PASSWORD 'streaming_geo_password';

-- Create the debezium user for CDC (requires REPLICATION for logical replication)
CREATE USER debezium WITH REPLICATION LOGIN PASSWORD 'debezium';
CREATE USER extract_pipeline_user with REPLICATION LOGIN PASSWORD 'extract_pipeline_user';

-- WAL read privileges for CDC users (group role, no login)
CREATE ROLE wal_reader_role NOLOGIN;

-- Grant connect privilege on the geo database
GRANT CONNECT ON DATABASE geo TO streaming_geo;
GRANT CONNECT ON DATABASE geo TO wal_reader_role;

GRANT wal_reader_role TO debezium;
GRANT wal_reader_role TO extract_pipeline_user;

