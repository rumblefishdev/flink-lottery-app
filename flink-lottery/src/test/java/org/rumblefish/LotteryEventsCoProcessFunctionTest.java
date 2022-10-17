package org.rumblefish;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.streaming.util.KeyedTwoInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses;
import org.junit.Before;
import org.junit.Test;
import org.rumblefish.event.LotteryEvent;
import org.rumblefish.event.UserEvent;
import org.rumblefish.event.lotteryclickevent.UserClickEvent;
import org.rumblefish.event.lotterystartevent.LotteryStartEvent;
import org.rumblefish.event.userbuycouponevent.UserBuyCouponEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LotteryEventsCoProcessFunctionTest {
    private final long DUMMY_TIMESTAMP = 10;

    KeyedTwoInputStreamOperatorTestHarness<Integer, LotteryEvent, UserEvent, EventOutput> harness;

    @Before
    public void beforeClass() throws Exception {
        KeyedCoProcessFunction<Integer, LotteryEvent, UserEvent, EventOutput> lotteryProcessFunction = new LotteryEventsCoProcessFunction();

        harness = ProcessFunctionTestHarnesses.forKeyedCoProcessFunction(
                lotteryProcessFunction, LotteryEvent::getLotteryId, UserEvent::getUserId, TypeInformation.of(Integer.class));

        harness.open();
    }

    @Test
    public void shouldNotStartLotteryIfItsAlreadyStarted() throws Exception {
        //when: lottery is already started
        harness.processElement1(new LotteryStartEvent(1), DUMMY_TIMESTAMP);

        //then: during the same action exception should be thrown
        Exception exception = assertThrows(IllegalStateException.class,
                () -> harness.processElement1(new LotteryStartEvent(1), DUMMY_TIMESTAMP));

        String expectedMessage = "Cannot start already started lottery, lotteryId: 1";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void userCannotBuyNegativeAmountOfCoupon() {
        //when: user is trying to buy negative amount of coupons, exception should be thrown
        Exception exception = assertThrows(IllegalStateException.class,
                () -> harness.processElement2(new UserBuyCouponEvent(1, -1), DUMMY_TIMESTAMP));

        String expectedMessage = "Cannot buy negative amount of coupons";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void userCannotClickOnInactiveLottery() {
        //when: user is trying to click on inactive lottery, exception should be thrown
        Exception exception = assertThrows(IllegalStateException.class,
                () -> harness.processElement2(new UserClickEvent(1000, 1, 1), DUMMY_TIMESTAMP));

        String expectedMessage = "Cannot click on inactive lottery, lotteryId: 1";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void userCannotClickOnActiveLotteryIfAmountOfCouponIsLowerThanOne() throws Exception {
        //given: lottery is activating
        harness.processElement1(new LotteryStartEvent(1), DUMMY_TIMESTAMP);

        //when: user is trying to click on lottery but without available coupons, exception should be thrown
        Exception exception = assertThrows(IllegalStateException.class,
                () -> harness.processElement2(new UserClickEvent(1000, 1, 1), DUMMY_TIMESTAMP));

        String expectedMessage = "User has no coupon, userId: 1";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void userClickEventShouldBeCollected() throws Exception {
        //given: lottery is activated and user has available coupon
        harness.processElement1(new LotteryStartEvent(1), DUMMY_TIMESTAMP);
        harness.processElement2(new UserBuyCouponEvent(1, 100), DUMMY_TIMESTAMP);

        //when: userClickEvent is sent
        harness.processElement2(new UserClickEvent(1000, 1, 1), DUMMY_TIMESTAMP);

        //then: only userClickEvent should be collected
        assertEquals(harness.extractOutputValues(), List.of(new EventOutput(1, 1, 1000)));
    }
}