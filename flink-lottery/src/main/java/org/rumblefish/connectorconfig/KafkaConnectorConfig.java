package org.rumblefish.connectorconfig;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

import java.util.Properties;

public class KafkaConnectorConfig implements ConnectorConfig {

    public SinkFunction<String> createSink() {
        return new FlinkKafkaProducer011<>("localhost:9092", "lottery-output-topic", new SimpleStringSchema());
    }

    public DataStream<String> createSource(StreamExecutionEnvironment env, String inputTopic) {
        FlinkKafkaConsumer011<String> kafkaConsumer = createStringConsumerForTopic(inputTopic);
        return env.addSource(kafkaConsumer);
    }

    @Override
    public String getFirstSourceInput() {
        return "lottery-input-topic";
    }

    @Override
    public String getSecondSourceInput() {
        return "user-input-topic";
    }

    private FlinkKafkaConsumer011<String> createStringConsumerForTopic(String topic) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "bid-club");
        props.setProperty("auto.offset.reset", "latest");
        props.setProperty("enable.auto.commit", "false");

        return new FlinkKafkaConsumer011<>(topic, new SimpleStringSchema(), props);
    }
}
