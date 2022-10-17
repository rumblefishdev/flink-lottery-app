package org.rumblefish.event.userbuycouponevent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.rumblefish.event.UserEvent;
import org.rumblefish.state.LotteryState;
import org.rumblefish.state.UserState;

@Getter
@NoArgsConstructor
@ToString
public class UserBuyCouponEvent extends UserEvent {
    private int purchasedCoupons;

    public UserBuyCouponEvent(Integer userId, int purchasedCoupons) {
        super(UserBuyCouponEvent.class.getSimpleName(), false, null, userId, null);
        this.purchasedCoupons = purchasedCoupons;
    }

    @Override
    public UserState process(UserState currentUserState, LotteryState currentLotteryState) {
        if (purchasedCoupons < 1) throw new IllegalStateException("Cannot buy negative amount of coupons");
        return currentUserState.addCoupons(purchasedCoupons);
    }
}
