package org.rumblefish.connectorconfig;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.io.IOException;

public interface ConnectorConfig {

    SinkFunction<String> createSink() throws IOException;

    DataStream<String> createSource(StreamExecutionEnvironment env, String input) throws IOException;

    String getFirstSourceInput();

    String getSecondSourceInput();
}
