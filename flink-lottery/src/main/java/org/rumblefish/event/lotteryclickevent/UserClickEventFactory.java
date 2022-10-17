package org.rumblefish.event.lotteryclickevent;

import com.google.gson.Gson;
import org.rumblefish.event.EventFactory;

public class UserClickEventFactory implements EventFactory<UserClickEvent> {
    private final static Gson mapper = new Gson();

    @Override
    public UserClickEvent createEvent(String eventAsJson) {
        return mapper.fromJson(eventAsJson, UserClickEvent.class);
    }

}
