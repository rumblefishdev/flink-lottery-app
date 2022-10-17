package org.rumblefish.connectorconfig;

import lombok.Getter;

@Getter
public enum ConnectorType {
    KAFKA_CONNECTOR("kafka-connector"),
    KINESIS_CONNECTOR("kinesis-connector");

    private final String value;

    ConnectorType(String value) {
        this.value = value;
    }
}
