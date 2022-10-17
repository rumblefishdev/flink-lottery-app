package org.rumblefish.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class LotteryState {
    private final Integer lotteryId;
    private final boolean isActive;

    public static LotteryState createNew(Integer lotteryId) {
        return new LotteryState(lotteryId, false);
    }

    public LotteryState startLottery() {
        return new LotteryState(lotteryId, true);
    }
}
