package org.rumblefish;

import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GetWinnerWindowFunction extends ProcessWindowFunction<EventOutput, Integer, Integer, TimeWindow> {

    @Override
    public void process(Integer integer, ProcessWindowFunction<EventOutput, Integer, Integer, TimeWindow>.Context context,
                        Iterable<EventOutput> elements, Collector<Integer> collector) {
        List<EventOutput> elementsAsList = new ArrayList<>();
        elements.iterator().forEachRemaining(elementsAsList::add);

        Random rand = new Random();
        Integer winnerId = elementsAsList.get(rand.nextInt(elementsAsList.size())).getUserId();
        collector.collect(winnerId);
    }
}
