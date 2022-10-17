package org.rumblefish.event;

public interface EventFactory<T extends Event> {
    T createEvent(String eventAsJson);
}
