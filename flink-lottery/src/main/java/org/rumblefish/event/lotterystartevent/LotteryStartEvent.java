package org.rumblefish.event.lotterystartevent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.rumblefish.event.LotteryEvent;
import org.rumblefish.state.LotteryState;

@Getter
@NoArgsConstructor
@ToString
public class LotteryStartEvent extends LotteryEvent {

    public LotteryStartEvent(int lotteryId) {
        super(LotteryStartEvent.class.getSimpleName(), false, lotteryId);
    }

    @Override
    public LotteryState process(LotteryState currentLotteryState) {
        if (currentLotteryState.isActive())
            throw new IllegalStateException("Cannot start already started lottery, lotteryId: " + currentLotteryState.getLotteryId());

        return currentLotteryState.startLottery();
    }
}
