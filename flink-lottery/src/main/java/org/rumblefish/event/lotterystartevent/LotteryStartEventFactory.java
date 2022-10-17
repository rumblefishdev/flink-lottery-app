package org.rumblefish.event.lotterystartevent;

import com.google.gson.Gson;
import org.rumblefish.event.EventFactory;

public class LotteryStartEventFactory implements EventFactory<LotteryStartEvent> {
    private final static Gson mapper = new Gson();

    @Override
    public LotteryStartEvent createEvent(String eventAsJson) {
        return mapper.fromJson(eventAsJson, LotteryStartEvent.class);
    }
}
