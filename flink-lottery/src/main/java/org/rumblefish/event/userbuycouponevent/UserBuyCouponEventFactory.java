package org.rumblefish.event.userbuycouponevent;

import com.google.gson.Gson;
import org.rumblefish.event.EventFactory;

public class UserBuyCouponEventFactory implements EventFactory<UserBuyCouponEvent> {
    private final static Gson mapper = new Gson();

    @Override
    public UserBuyCouponEvent createEvent(String eventAsJson) {
        return mapper.fromJson(eventAsJson, UserBuyCouponEvent.class);
    }
}
