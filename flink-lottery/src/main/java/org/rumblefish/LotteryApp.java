package org.rumblefish;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.rumblefish.connectorconfig.ConnectorConfig;
import org.rumblefish.connectorconfig.ConnectorType;
import org.rumblefish.connectorconfig.KafkaConnectorConfig;

public class LotteryApp {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.enableCheckpointing(1000);

        ConnectorConfig connectorConfig = assignConnector(args);

        DataStream<String> lotteryStream = connectorConfig.createSource(env, connectorConfig.getFirstSourceInput());
        DataStream<String> userStream = connectorConfig.createSource(env, connectorConfig.getSecondSourceInput());

        SinkFunction<String> lotteryEventsProducer = connectorConfig.createSink();

        LotteryStreams.prepareLotteryPipeline(lotteryStream, userStream, lotteryEventsProducer);

        env.execute("lottery");
    }

    private static ConnectorConfig assignConnector(String[] args) {
        String connector = args[0];
        if (connector.equals(ConnectorType.KAFKA_CONNECTOR.getValue()))
            return new KafkaConnectorConfig();
        if (connector.equals(ConnectorType.KINESIS_CONNECTOR.getValue()))
            return new KafkaConnectorConfig();
        else {
            throw new IllegalStateException("Connector does not exists");
        }
    }
}
