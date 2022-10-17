package org.rumblefish.connectorconfig;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisProducer;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class KinesisConnectorConfig implements ConnectorConfig {

    @Override
    public SinkFunction<String> createSink() throws IOException {
        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();
        String lotteryOutputStreamName = (String) applicationProperties.get("LotteryOutputStream").get("name");
        FlinkKinesisProducer<String> clicksAnalyticsProducer =
                new FlinkKinesisProducer<>(new SimpleStringSchema(), applicationProperties.get("ProducerConfigProperties"));
        clicksAnalyticsProducer.setDefaultStream(lotteryOutputStreamName);
        clicksAnalyticsProducer.setCustomPartitioner(new CustomKinesisPartitioner());

        return clicksAnalyticsProducer;
    }

    @Override
    public DataStream<String> createSource(StreamExecutionEnvironment env, String inputStream) throws IOException {
        Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();
        String bidPurchasesStreamName = (String) applicationProperties.get(inputStream).get("name");

        return env.addSource(
                new FlinkKinesisConsumer<>(bidPurchasesStreamName,
                        new SimpleStringSchema(), applicationProperties.get("ConsumerConfigProperties")))
                .uid(inputStream + "_uid");
    }

    @Override
    public String getFirstSourceInput() {
        return "LotteryEventsInputStream";
    }

    @Override
    public String getSecondSourceInput() {
        return "UserEventsInputStream";
    }
}
