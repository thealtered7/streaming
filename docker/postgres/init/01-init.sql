-- Create the geo database
CREATE DATABASE geo;

-- Create the streaming_geo user
CREATE USER streaming_geo WITH PASSWORD 'streaming_geo_password';

-- Create the debezium user for CDC (requires REPLICATION for logical replication)
CREATE USER debezium WITH REPLICATION LOGIN PASSWORD 'debezium';

-- Grant connect privilege on the geo database
GRANT CONNECT ON DATABASE geo TO streaming_geo;
GRANT CONNECT ON DATABASE geo TO debezium;

