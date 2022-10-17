package org.rumblefish.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class UserState {
    private Integer userId;
    private Integer availableCoupon;

    public static UserState createNew(Integer userId) {
        return new UserState(userId, 0);
    }

    public UserState decrementCoupon() {
        return new UserState(userId, availableCoupon--);
    }

    public UserState addCoupons(int purchasedCoupons) {
        return new UserState(userId, availableCoupon + purchasedCoupons);
    }
}
