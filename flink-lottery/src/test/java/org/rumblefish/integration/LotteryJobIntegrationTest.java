package org.rumblefish.integration;

import com.google.gson.Gson;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.junit.ClassRule;
import org.junit.Test;
import org.rumblefish.LotteryStreams;
import org.rumblefish.event.lotteryclickevent.UserClickEvent;
import org.rumblefish.event.lotterystartevent.LotteryStartEvent;
import org.rumblefish.event.userbuycouponevent.UserBuyCouponEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LotteryJobIntegrationTest {

    @ClassRule
    public static MiniClusterWithClientResource flinkCluster =
            new MiniClusterWithClientResource(
                    new MiniClusterResourceConfiguration.Builder()
                            .setNumberSlotsPerTaskManager(2)
                            .setNumberTaskManagers(1)
                            .build());

    @Test
    public void testLotteryPipeline() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        Gson mapper = new Gson();

        CollectSink.values.clear();

        DataStreamSource<String> lotteryDataStreamSource = env.fromElements(
                mapper.toJson(new LotteryStartEvent(1))
        );

        DataStreamSource<String> userDataStreamSource = env.fromElements(
                mapper.toJson(new UserBuyCouponEvent(1, 100)),
                mapper.toJson(new UserBuyCouponEvent(2, 100)),
                mapper.toJson(new UserBuyCouponEvent(3, 100)),
                mapper.toJson(new UserClickEvent(1000, 1, 1)),
                mapper.toJson(new UserClickEvent(5000, 2, 1)),
                mapper.toJson(new UserClickEvent(11000, 3, 1))
        );

        LotteryStreams.prepareLotteryPipeline(lotteryDataStreamSource, userDataStreamSource, new CollectSink());

        env.execute();

        assertEquals(CollectSink.values.size(), 2);
        assertTrue(CollectSink.values.contains("3"));
    }

    private static class CollectSink implements SinkFunction<String> {

        public static final List<String> values = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void invoke(String value, Context context) {
            values.add(value);
        }
    }
}
