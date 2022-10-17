package org.rumblefish.event;

import com.google.gson.Gson;
import org.rumblefish.event.lotteryclickevent.UserClickEvent;
import org.rumblefish.event.lotteryclickevent.UserClickEventFactory;
import org.rumblefish.event.lotterystartevent.LotteryStartEvent;
import org.rumblefish.event.lotterystartevent.LotteryStartEventFactory;
import org.rumblefish.event.userbuycouponevent.UserBuyCouponEvent;
import org.rumblefish.event.userbuycouponevent.UserBuyCouponEventFactory;

import java.util.HashMap;
import java.util.Map;


public class CompositeEventFactory {
    private static final Map<String, EventFactory<? extends Event>> registeredFactories = new HashMap<>();
    private static final Gson mapper = new Gson();

    static {
        registerFactory(LotteryStartEvent.class.getSimpleName(), new LotteryStartEventFactory());
        registerFactory(UserClickEvent.class.getSimpleName(), new UserClickEventFactory());
        registerFactory(UserBuyCouponEvent.class.getSimpleName(), new UserBuyCouponEventFactory());
    }

    public static void registerFactory(String type, EventFactory<? extends Event> factory) {
        registeredFactories.put(type, factory);
    }

    public static Event createEvent(String eventAsJson) {
        Event event = mapper.fromJson(eventAsJson, Event.class);
        EventFactory<? extends Event> factory = registeredFactories.get(event.getType());
        if (factory == null) throw new IllegalArgumentException("Factory does not exists");

        return factory.createEvent(eventAsJson);
    }
}
