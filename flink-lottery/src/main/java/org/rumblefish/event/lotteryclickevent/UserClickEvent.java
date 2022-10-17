package org.rumblefish.event.lotteryclickevent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.rumblefish.event.UserEvent;
import org.rumblefish.state.LotteryState;
import org.rumblefish.state.UserState;

@Getter
@NoArgsConstructor
@ToString
public class UserClickEvent extends UserEvent {

    public UserClickEvent(long timestamp, Integer userId, Integer lotteryId) {
        super(UserClickEvent.class.getSimpleName(), true, timestamp, userId, lotteryId);
    }

    @Override
    public UserState process(UserState currentUserState, LotteryState currentLotteryState) {
        if (!currentLotteryState.isActive())
            throw new IllegalStateException("Cannot click on inactive lottery, lotteryId: " + currentLotteryState.getLotteryId());
        if (currentUserState.getAvailableCoupon() < 1)
            throw new IllegalStateException("User has no coupon, userId: " + currentUserState.getUserId());

        return currentUserState.decrementCoupon();
    }


}
