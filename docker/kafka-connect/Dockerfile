# Confluent Kafka Connect with Avro support + Debezium PostgreSQL connector
FROM confluentinc/cp-kafka-connect:7.5.0

# Install Debezium PostgreSQL connector from Maven (not on Confluent Hub)
RUN curl -sSfL https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/2.5.4.Final/debezium-connector-postgres-2.5.4.Final-plugin.tar.gz | tar -xzf - -C /usr/share/confluent-hub-components/
