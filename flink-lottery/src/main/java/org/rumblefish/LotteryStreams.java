package org.rumblefish;

import com.google.gson.Gson;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.rumblefish.event.LotteryEvent;
import org.rumblefish.event.UserEvent;

import java.io.Serializable;

import static org.rumblefish.event.CompositeEventFactory.createEvent;

public class LotteryStreams implements Serializable {

    public static void prepareLotteryPipeline(DataStream<String> lotteryStream, DataStream<String> userStream, SinkFunction<String> LotterySink) {
        SingleOutputStreamOperator<EventOutput> processedEvents = prepareConnectedStream(lotteryStream, userStream)
                .process(new LotteryEventsCoProcessFunction());

        processedEvents
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<EventOutput>forMonotonousTimestamps().
                                withTimestampAssigner((event, timestamp) -> event.getTimestamp()))
                .keyBy(EventOutput::getLotteryId)
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .process(new GetWinnerWindowFunction())
                .map(event -> new Gson().toJson(event))
                .addSink(LotterySink);
    }

    private static ConnectedStreams<LotteryEvent, UserEvent> prepareConnectedStream(DataStream<String> lotteryEvents, DataStream<String> userEvents) {
        return lotteryEvents
                .map(lotteryEventAsString -> (LotteryEvent) createEvent(lotteryEventAsString))
                .keyBy(LotteryEvent::getLotteryId)
                .connect(
                        userEvents
                                .map(userEventAsString -> (UserEvent) createEvent(userEventAsString))
                                .keyBy(UserEvent::getUserId));
    }


}
