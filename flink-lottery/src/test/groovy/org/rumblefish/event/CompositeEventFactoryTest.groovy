package org.rumblefish.event

import org.rumblefish.event.lotteryclickevent.UserClickEvent
import org.rumblefish.event.lotterystartevent.LotteryStartEvent
import org.rumblefish.event.userbuycouponevent.UserBuyCouponEvent
import spock.lang.Specification

class CompositeEventFactoryTest extends Specification {

    def "Should create lottery start event"() {
        given: 'Lottery start event as string is created'
        String eventAsString = "{\"type\": \"LotteryStartEvent\", \"lotteryId\": 1, \"isOutputEvent\": false}"

        when: 'event is creating'
        def event = CompositeEventFactory.createEvent(eventAsString)

        then: 'Event should be correctly created'
        event == new LotteryStartEvent(1)
    }

    def "Should create user click event"() {
        given: 'User click event as string is created'
        String eventAsString = "{\"type\": \"UserClickEvent\", \"lotteryId\": 1, \"userId\": 1, \"isOutputEvent\": true}"

        when: 'event is creating'
        def event = CompositeEventFactory.createEvent(eventAsString)

        then: 'Event should be correctly created'
        event == new UserClickEvent(1000, 1, 1)
    }

    def "Should create user buy coupon event"() {
        given: 'User click event as string is created'
        String eventAsString = "{\"type\": \"UserBuyCouponEvent\", \"lotteryId\": 1, \"userId\": 1, \"isOutputEvent\": false, \"purchasedCoupons\": 100}"

        when: 'event is creating'
        def event = CompositeEventFactory.createEvent(eventAsString)

        then: 'Event should be correctly created'
        event == new UserBuyCouponEvent(1, 100)
    }
}
