package org.rumblefish;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.runtime.operators.windowing.WindowOperator;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalIterableProcessWindowFunction;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class GetWinnerWindowFunctionTest {
    private static final TypeInformation<EventOutput> EVENT_OUTPUT_TYPE_INFORMATION = TypeInformation.of(new TypeHint<>() {
    });

    @Test
    public void processFunctionShouldReturnExactlyOneElementPerTimeWindow() throws Exception {
        //given: window operator is created
        WindowOperator<Integer, EventOutput, Iterable<EventOutput>, Integer, TimeWindow> operator = getWindowOperator();
        OneInputStreamOperatorTestHarness<EventOutput, Integer>
                testHarness = new KeyedOneInputStreamOperatorTestHarness<>(operator, new EventOutputKeySelector(), BasicTypeInfo.INT_TYPE_INFO);
        testHarness.open();

        //when: Event outputs are processing
        testHarness.processElement(new StreamRecord<>(new EventOutput(1, 1, 1000), 1000));
        testHarness.processElement(new StreamRecord<>(new EventOutput(1, 2, 2000), 2000));
        testHarness.processElement(new StreamRecord<>(new EventOutput(1, 3, 3000), 3000));
        testHarness.processElement(new StreamRecord<>(new EventOutput(1, 4, 6000), 6000));
        testHarness.processElement(new StreamRecord<>(new EventOutput(1, 1, 7000), 7000));
        testHarness.processWatermark(new Watermark(10000));

        //then: exactly two output is produces, the third one is because Watermark
        assertEquals(testHarness.getOutput().size(), 3);

        testHarness.close();
    }

    private WindowOperator<Integer, EventOutput, Iterable<EventOutput>, Integer, TimeWindow> getWindowOperator() {
        ListStateDescriptor<EventOutput> stateDesc = new ListStateDescriptor<>(
                "window-contents",
                EVENT_OUTPUT_TYPE_INFORMATION.createSerializer(new ExecutionConfig()));

        return new WindowOperator<>(
                TumblingEventTimeWindows.of(Time.of(5, TimeUnit.SECONDS)),
                new TimeWindow.Serializer(),
                new EventOutputKeySelector(),
                BasicTypeInfo.INT_TYPE_INFO.createSerializer(new ExecutionConfig()),
                stateDesc,
                new InternalIterableProcessWindowFunction<>(new GetWinnerWindowFunction()),
                EventTimeTrigger.create(),
                0,
                null);
    }

    private static class EventOutputKeySelector implements KeySelector<EventOutput, Integer> {
        private static final long serialVersionUID = 1L;

        @Override
        public Integer getKey(EventOutput value) {
            return value.getLotteryId();
        }
    }
}