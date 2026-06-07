# Confluent Kafka Connect with Avro support + Debezium PostgreSQL connector
FROM confluentinc/cp-kafka-connect:7.5.0

USER root

# Install Debezium PostgreSQL connector from Maven (not on Confluent Hub)
RUN curl -sSfL https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/2.5.4.Final/debezium-connector-postgres-2.5.4.Final-plugin.tar.gz | tar -xzf - -C /usr/share/confluent-hub-components/

# Prometheus JMX exporter for Connect / Debezium metrics
RUN mkdir -p /usr/share/jmx /etc/jmx && \
    curl -sSfL -o /usr/share/jmx/jmx_prometheus_javaagent.jar \
      https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.20.0/jmx_prometheus_javaagent-0.20.0.jar

COPY jmx-config.yml /etc/jmx/kafka-connect.yml

USER appuser
