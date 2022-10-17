package org.rumblefish.connectorconfig;

import com.google.gson.Gson;
import org.apache.flink.streaming.connectors.kinesis.KinesisPartitioner;
import org.rumblefish.event.Event;

public class CustomKinesisPartitioner extends KinesisPartitioner<String> {

    @Override
    public String getPartitionId(String key) {
        return Integer.toString(new Gson().fromJson(key, Event.class).getId());
    }
}
